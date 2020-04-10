package xyz.liblnd.messagerelayer;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VanishListener implements Listener
{
    private final MessageRelayer plugin;

    VanishListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHide(PlayerHideEvent event)
    {
        plugin.handleLeave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShow(PlayerShowEvent event)
    {
        plugin.handleJoin(event.getPlayer());
    }
}
