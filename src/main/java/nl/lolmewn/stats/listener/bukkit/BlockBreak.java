package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.BukkitUtil;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class BlockBreak implements Listener {

    public BlockBreak(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()).subscribe(player ->
            StatManager.getInstance().getStat("Blocks broken").ifPresent(stat -> {
                Map<String, Object> metadata = generateMetadata(event);
                player.getStats(stat).addEntry(
                        new StatTimeEntry(System.currentTimeMillis(), 1, metadata)
                );
            }), Util::handleError
        );
    }

    private Map<String, Object> generateMetadata(BlockBreakEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("world", event.getBlock().getWorld().getUID().toString());
        map.put("material", BukkitUtil.getMaterialType(event.getBlock().getType()));
        map.put("tool", BukkitUtil.getItemType(event.getPlayer().getInventory().getItemInMainHand()));
        return map;
    }
}
