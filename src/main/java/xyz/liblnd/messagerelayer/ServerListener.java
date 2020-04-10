package xyz.liblnd.messagerelayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerListener implements Listener
{
    private final MessageRelayer plugin;

    ServerListener(MessageRelayer plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event)
    {
        if(!(event.getType() == ServerLoadEvent.LoadType.STARTUP))
            return;

        plugin.sendMessage(null, "\u2705 Server has started");
    }
}
