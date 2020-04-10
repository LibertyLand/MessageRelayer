package xyz.liblnd.messagerelayer;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TownyListener implements Listener
{
    private final MessageRelayer plugin;

    TownyListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatHook(AsyncChatHookEvent event)
    {
        if(!(event.getChannel().getName().equals("general")))
            return;

        plugin.sendMessage(event.getPlayer(), event.getMessage());
    }
}
