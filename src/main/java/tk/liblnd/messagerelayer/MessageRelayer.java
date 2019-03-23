package tk.liblnd.messagerelayer;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class MessageRelayer extends JavaPlugin implements Listener
{
    private Logger LOG;
    private OkHttpClient client;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String avatarBase = "https://crafatar.com/avatars/%s?overlay";
    private Config config;

    @Override
    public void onEnable()
    {
        this.LOG = this.getLogger();
        LOG.info("Loading MessageRelayer...");
        this.config = new Config(getConfig());
        saveConfig();

        this.client = new OkHttpClient();
        this.getServer().getPluginManager().registerEvents(this, this);
        LOG.info(ChatColor.GREEN+"MessageRelayer has been enabled!");
    }

    @Override
    public void onDisable()
    {
        client.dispatcher().executorService().shutdown();
        LOG.info("MessageRelayer has been disabled");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatHook(AsyncChatHookEvent event)
    {
        Player player = event.getPlayer();
        if(!(event.getChannel().getName().equals("general")))
                return;

        String toSend = sanitize(event.getMessage());
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if(!(Bukkit.getPluginManager().getPlugin("TownyChat")==null))
            return;

        String toSend = sanitize(event.getMessage());
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE5 **"+player.getName()+"** has joined the server!");
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE4 **"+player.getName()+"** has left the server!");
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        String deathMsg = event.getDeathMessage();
        Player player = event.getEntity();

        String toSend = sanitize("\uD83D\uDC80 "+deathMsg);
        String avatar = String.format(avatarBase, player.getUniqueId().toString());

        sendMessage(prepareJSON(toSend, player.getName(), avatar));
    }

    private JSONObject prepareJSON(String content, String username, String avatar)
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

    private String sanitize(String msg)
    {
        return msg.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
    }

    private void sendMessage(JSONObject json)
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
            public void onResponse(Call call, Response response)
            {
                if(!(response.isSuccessful()))
                    onFailure(call, new IOException("Could not send webhook message. HTTP code: " + response.code()));
                response.close();
            }

            @Override
            public void onFailure(Call call, IOException e)
            {
                getLogger().severe("Exception whilst sending a webhook message: ");
                e.printStackTrace();
            }
        });
    }
}
