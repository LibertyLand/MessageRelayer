package xyz.liblnd.messagerelayer;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageRelayer extends JavaPlugin
{
    private WebhookClient client;

    @Override
    public void onEnable()
    {
        getLogger().info("Loading MessageRelayer...");
        Config config = new Config(getConfig());
        saveConfig();

        this.client = new WebhookClientBuilder(config.getUrl())
                .setThreadFactory(job ->
                {
                    Thread thread = new Thread(job);
                    thread.setName("MessageRelayer - Sending Thread");
                    thread.setDaemon(true);
                    return thread;
                })
                .setWait(false)
                .build();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PluginListener(this), this);

        if(pluginManager.isPluginEnabled("TownyChat"))
            pluginManager.registerEvents(new TownyListener(this), this);

        if(pluginManager.isPluginEnabled("PremiumVanish"))
            pluginManager.registerEvents(new VanishListener(this), this);

        getLogger().info("MessageRelayer has been enabled!");
    }

    @Override
    public void onDisable()
    {
        client.close();
        getLogger().info("MessageRelayer has been disabled");
    }

    void sendMessage(Player player, String message)
    {
        message = sanitize(message);

        client.send(new WebhookMessageBuilder()
                .setAvatarUrl(String.format("https://crafatar.com/avatars/%s?overlay", player.getUniqueId()))
                .setUsername(player.getName())
                .setContent(message)
                .build());
    }

    void handleJoin(Player player)
    {
        if(isVanished(player))
            return;

        sendMessage(player, "\uD83D\uDCE5 **" + player.getName() + "** has joined the server!");
    }

    void handleLeave(Player player)
    {
        if(isVanished(player))
            return;

        sendMessage(player, "\uD83D\uDCE4 **" + player.getName() + "** has left the server!");
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
        return ChatColor.stripColor(msg.replace("@everyone", "@\u0435veryone")
                .replace("@here", "@h\u0435re")).trim();
    }
}
