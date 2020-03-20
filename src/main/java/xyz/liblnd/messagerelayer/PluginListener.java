package xyz.liblnd.messagerelayer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PluginListener implements Listener
{
    private MessageRelayer plugin;

    PluginListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        if(!(Bukkit.getPluginManager().getPlugin("TownyChat") == null))
            return;

        plugin.sendMessage(event.getPlayer(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.handleLeave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        String deathMsg = event.getDeathMessage();
        if(deathMsg == null)
            return;

        plugin.sendMessage(event.getEntity(), "\uD83D\uDC80 " + deathMsg);
    }
}
