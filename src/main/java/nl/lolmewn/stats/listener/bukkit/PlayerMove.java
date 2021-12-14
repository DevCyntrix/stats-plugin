package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMove implements Listener, Runnable {

    private static class PlayerMoveType {
        protected static final String VEHICLE = "Vehicle";
        protected static final String SWIMMING = "Swimming";
        protected static final String GLIDING = "Gliding";
        protected static final String FLYING = "Flying";
        protected static final String SPRINTING = "Sprinting";
        protected static final String SNEAKING = "Sneaking";
        protected static final String JUMPING = "Jumping";
        protected static final String FALLING = "Falling";
        protected static final String WALKING = "Walking";

        protected static String getVehicleType(EntityType type) {
            return type.toString();
        }
    }

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

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        double distance;

        String moveType = getMoveType(player, from, to);

        String cache = player.getUniqueId()
                + from.getWorld().getUID().toString()
                + moveType;

        distance = from.distance(to);

        cacheMap.merge(cache, distance, Double::sum);
    }

    private String getMoveType(Player player, Location from, Location to) {
        if (player.isInsideVehicle()) {
            return PlayerMoveType.getVehicleType(player.getVehicle().getType());
        }

        if (player.isSwimming()) {
            return PlayerMoveType.SWIMMING;
        }

        if (player.isGliding()) {
            return PlayerMoveType.GLIDING;
        }

        if (player.isFlying()) {
            return PlayerMoveType.FLYING;
        }

        if (player.isSprinting()) {
            return PlayerMoveType.SPRINTING;
        }

        if (player.isSneaking()) {
            return PlayerMoveType.SNEAKING;
        }

        if(from.getY() < to.getY() && (from.getBlock().getType().equals(Material.AIR) || to.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR))) {
            return PlayerMoveType.JUMPING;
        }

        if (from.getY() > to.getY() && from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return PlayerMoveType.FALLING;
        }

        return PlayerMoveType.WALKING;
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
                                                Util.of("world", worldUuid, "type", moveType))
                                )
                            );
                        }
                )
        );
        this.cacheMap.clear();
    }

}
