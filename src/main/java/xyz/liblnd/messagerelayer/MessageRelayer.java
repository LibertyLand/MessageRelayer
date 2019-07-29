package xyz.liblnd.messagerelayer;

import okhttp3.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MessageRelayer extends JavaPlugin
{
    final String avatarBase = "https://crafatar.com/avatars/%s?overlay";

    private OkHttpClient client;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Config config;

    @Override
    public void onEnable()
    {
        getLogger().info("Loading MessageRelayer...");
        this.config = new Config(getConfig());
        saveConfig();

        this.client = new OkHttpClient();

        getServer().getPluginManager().registerEvents(new PluginListener(this), this);

        if(getServer().getPluginManager().isPluginEnabled("TownyChat"))
            getServer().getPluginManager().registerEvents(new TownyListener(this), this);

        if(getServer().getPluginManager().isPluginEnabled("PremiumVanish"))
            getServer().getPluginManager().registerEvents(new VanishListener(this), this);

        getLogger().info(ChatColor.GREEN + "MessageRelayer has been enabled!");
    }

    @Override
    public void onDisable()
    {
        client.dispatcher().executorService().shutdown();
        getLogger().info("MessageRelayer has been disabled");
    }

    @SuppressWarnings("unchecked")
    JSONObject prepareJSON(String content, String username, String avatar)
    {
        JSONObject obj = new JSONObject();

        obj.put("content", content);
        obj.put("username", username);
        obj.put("avatar_url", avatar);

        return obj;
    }

    private boolean isVanished(Player player)
    {
        for(MetadataValue meta : player.getMetadata("vanished"))
        {
            if(meta.asBoolean())
                return true;
        }

        return false;
    }

    String sanitize(String msg)
    {
        return ChatColor.stripColor(msg.replace("@everyone", "@\u0435veryone")
                .replace("@here", "@h\u0435re")).trim();
    }

    void sendMessage(JSONObject json)
    {
        if(config.getUrl().equals("https://canary.discordapp.com/api/webhooks"))
            return;

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(config.getUrl())
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response)
            {
                if(!(response.isSuccessful()))
                    onFailure(call, new IOException("Could not send webhook message. HTTP code: " + response.code()));

                response.close();
            }

            @Override
            public void onFailure(@Nonnull Call call, IOException e)
            {
                getLogger().severe("Exception whilst sending a webhook message: " + e);
                e.printStackTrace();
            }
        });
    }

    void handleJoin(Player player)
    {
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE5 **" + player.getName() + "** has joined the server!");
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    void handleLeave(Player player)
    {
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE4 **" + player.getName() + "** has left the server!");
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }
}
