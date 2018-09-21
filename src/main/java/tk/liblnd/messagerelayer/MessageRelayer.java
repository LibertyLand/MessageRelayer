package tk.liblnd.messagerelayer;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        LOG.info("MessageRelayer has been disabled");
    }

    @EventHandler
    public void onChat(AsyncChatHookEvent event)
    {
        if(!(Bukkit.getPluginManager().getPlugin("TownyChat")==null))
        {
            if(!(event.getChannel().getName().equals("general")))
                return;
        }

        Player player = event.getPlayer();
        String message = sanitize(event.getMessage());

        JSONObject obj = new JSONObject();
        obj.put("content", message);
        obj.put("username", player.getName());
        obj.put("avatar_url", String.format(avatarBase, player.getUniqueId().toString()));

        sendMessage(obj);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE5 **"+player.getName()+"** has joined the server!");

        JSONObject obj = new JSONObject();
        obj.put("content", toSend);
        obj.put("username", player.getName());
        obj.put("avatar_url", String.format(avatarBase, player.getUniqueId().toString()));

        sendMessage(obj);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(isVanished(player))
            return;

        String toSend = sanitize("\uD83D\uDCE4 **"+player.getName()+"** has left the server!");

        JSONObject obj = new JSONObject();
        obj.put("content", toSend);
        obj.put("username", player.getName());
        obj.put("avatar_url", String.format(avatarBase, player.getUniqueId().toString()));

        sendMessage(obj);
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

        try
        {
            Response response = client.newCall(request).execute();
            response.close();
        }
        catch(IOException e)
        {
            LOG.severe("Could not make request to Discord! "+e.getMessage());
            e.printStackTrace();
        }
    }
}
