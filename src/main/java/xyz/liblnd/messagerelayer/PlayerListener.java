package xyz.liblnd.messagerelayer;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerListener implements Listener
{
    private final MessageRelayer plugin;

    PlayerListener(MessageRelayer plugin)
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

    // Mostly taken from DiscordSRV
    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event)
    {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();

        if(advancement == null || advancement.getKey().getKey().contains("recipe/") || player == null)
            return;

        if(plugin.isVanished(player))
            return;

        try
        {
            Object craftAdvancement = advancement.getClass().getMethod("getHandle").invoke(advancement);
            Object advancementDisplay = craftAdvancement.getClass().getMethod("c").invoke(craftAdvancement);
            boolean display = (boolean) advancementDisplay.getClass().getMethod("i").invoke(advancementDisplay);

            if(!(display))
                return;
        }
        catch(NullPointerException ignored) {return;}
        catch(Exception e)
        {
            plugin.getLogger().log(Level.SEVERE, "Could not do advancement reflection", e);
            return;
        }

        String rawName = advancement.getKey().getKey();
        String name = Arrays.stream(rawName.substring(rawName.lastIndexOf("/") + 1).toLowerCase().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));

        plugin.sendMessage(player, "\uD83C\uDFC5 Has made the advancement **" + name + "**");
    }
}
