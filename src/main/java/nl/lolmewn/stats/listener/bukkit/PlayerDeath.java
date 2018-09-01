package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PlayerDeath implements Listener {

    public PlayerDeath(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        PlayerManager.getInstance().getPlayer(event.getEntity().getUniqueId()).subscribe(player ->
                StatManager.getInstance().getStat("Deaths").ifPresent(stat ->
                        player.getStats(stat).addEntry(
                                new StatTimeEntry(System.currentTimeMillis(), 1, generateMetadata(event))
                        )
                ), Util::handleError);
    }

    private Map<String, Object> generateMetadata(PlayerDeathEvent event) {
        return Util.of("cause", event.getEntity().getLastDamageCause().getCause().toString(),
                "world", event.getEntity().getLocation().getWorld().getUID().toString(),
                "loc_x", event.getEntity().getLocation().getX(),
                "loc_y", event.getEntity().getLocation().getY(),
                "loc_z", event.getEntity().getLocation().getZ());
    }
}
