package tk.liblnd.messagerelayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        if(!(Bukkit.getPluginManager().getPlugin("TownyChat") == null))
            return;

        String toSend = plugin.sanitize(event.getMessage());
        String avatar = String.format(plugin.avatarBase, player.getUniqueId().toString());

        plugin.sendMessage(plugin.prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(plugin.isVanished(player))
            return;

        String toSend = plugin.sanitize("\uD83D\uDCE5 **" + player.getName() + "** has joined the server!");
        String avatar = String.format(plugin.avatarBase, player.getUniqueId().toString());

        plugin.sendMessage(plugin.prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(plugin.isVanished(player))
            return;

        String toSend = plugin.sanitize("\uD83D\uDCE4 **" + player.getName() + "** has left the server!");
        String avatar = String.format(plugin.avatarBase, player.getUniqueId().toString());

        plugin.sendMessage(plugin.prepareJSON(toSend, player.getName(), avatar));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        String deathMsg = event.getDeathMessage();
        Player player = event.getEntity();

        String toSend = plugin.sanitize("\uD83D\uDC80 " + deathMsg);
        String avatar = String.format(plugin.avatarBase, player.getUniqueId().toString());

        plugin.sendMessage(plugin.prepareJSON(toSend, player.getName(), avatar));
    }
}
