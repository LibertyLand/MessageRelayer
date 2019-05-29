package tk.liblnd.messagerelayer;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TownyListener implements Listener
{
    private MessageRelayer plugin;

    TownyListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatHook(AsyncChatHookEvent event)
    {
        Player player = event.getPlayer();
        if(!(event.getChannel().getName().equals("general")))
            return;

        String toSend = plugin.sanitize(event.getMessage());
        String avatar = String.format(plugin.avatarBase, player.getUniqueId().toString());

        plugin.sendMessage(plugin.prepareJSON(toSend, player.getName(), avatar));
    }
}
