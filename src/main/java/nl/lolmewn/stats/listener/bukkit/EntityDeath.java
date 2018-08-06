package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.BukkitUtil;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;

public class EntityDeath implements Listener {

    public EntityDeath(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return; // natural cause-ish.
        }
        if (event.getEntity().getKiller().hasMetadata("NPC")) {
            return;
        }
        Player player = event.getEntity().getKiller();
        PlayerManager.getInstance().getPlayer(player.getUniqueId()).subscribe(statsPlayer ->
                StatManager.getInstance().getStat("Kills").ifPresent(stat ->
                        statsPlayer.getStats(stat).addEntry(new StatTimeEntry(
                                System.currentTimeMillis(), 1,
                                getMetadata(event)
                        ))
                )
        );
    }

    private Map<String, Object> getMetadata(EntityDeathEvent event) {
        return Map.of("world", event.getEntity().getLocation().getWorld().getUID().toString(),
                "victimType", event.getEntityType().toString(),
                "victimName", Optional.ofNullable(event.getEntity().getCustomName()).orElse(event.getEntityType().toString()),
                "weapon", BukkitUtil.getWeaponName(event.getEntity().getKiller().getInventory().getItemInMainHand())
        );
    }
}
