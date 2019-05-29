package tk.liblnd.messagerelayer;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishListener implements Listener
{
    private MessageRelayer plugin;

    VanishListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHide(PlayerHideEvent event)
    {
        plugin.pluginListener.onPlayerQuit(new PlayerQuitEvent(event.getPlayer(), ""));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShow(PlayerShowEvent event)
    {
        plugin.pluginListener.onPlayerJoin(new PlayerJoinEvent(event.getPlayer(), ""));
    }
}
