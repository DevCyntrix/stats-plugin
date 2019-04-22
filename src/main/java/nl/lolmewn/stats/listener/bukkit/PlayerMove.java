package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMove implements Listener, Runnable {

    private Map<String, Double> cacheMap = new ConcurrentHashMap<>();

    public PlayerMove(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) {
            return;
        }
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }
        double distance = event.getFrom().distance(event.getTo());
        String cache = event.getPlayer().getUniqueId()
                + event.getFrom().getWorld().getUID().toString()
                + getMoveType(event.getPlayer());
        cacheMap.merge(cache, distance, Double::sum);
    }

    private String getMoveType(Player player) {
        if (player.isInsideVehicle()) {
            return player.getVehicle().getType().toString();
        }
//        if (player.isSwimming()) {
//            return "Swimming";
//        }
        if (player.isGliding()) {
            return "Gliding";
        }
        if (player.isFlying()) {
            return "Flying";
        }
        if (player.isSprinting()) {
            return "Sprinting";
        }
        if (player.isSneaking()) {
            return "Sneaking";
        }
        return "Walking";
    }

    @Override
    public void run() {
        StatManager.getInstance().getStat("Move").ifPresent(stat ->
                this.cacheMap.forEach((cache, value) -> {
                            String playerUuid = cache.substring(0, 36);
                            String worldUuid = cache.substring(36, 72);
                            String moveType = cache.substring(72);
                            PlayerManager.getInstance().getPlayer(UUID.fromString(playerUuid)).subscribe(statsPlayer ->
                                statsPlayer.getStats(stat).addEntry(
                                        new StatTimeEntry(System.currentTimeMillis(), value,
                                                Util.of("world", UUID.fromString(worldUuid), "type", moveType))
                                )
                            );
                        }
                )
        );
        this.cacheMap.clear();
    }

    private class PlayerMoveCache {
        private UUID player, world;
        private String moveType;

        PlayerMoveCache(UUID player, UUID world, String moveType) {
            this.player = player;
            this.world = world;
            this.moveType = moveType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerMoveCache that = (PlayerMoveCache) o;
            if (!player.equals(that.player)) return false;
            if (!world.equals(that.world)) return false;
            return moveType.equals(that.moveType);
        }
    }
}
