package nl.lolmewn.stats.signs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitStatsSign extends StatsSign {

    private Plugin plugin;

    public BukkitStatsSign(Plugin plugin, UUID uuid, int x, int y, int z, UUID world, StatsSignSpec spec) {
        super(uuid, x, y, z, world, spec,
                (Runnable runnable) -> plugin.getServer().getScheduler()
                        .runTaskTimer(plugin, runnable, 0, spec.getInterval() * 20));
        this.plugin = plugin;
    }

    @Override
    protected List<UUID> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
    }

    @Override
    protected void updateSign(String line1, String line2, String line3, String line4) {
        if (this.plugin == null) return;
        this.plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!this.isActive()) return;
            Sign sign = (Sign) Bukkit.getWorld(this.getWorld()).getBlockAt(this.getX(), this.getY(), this.getZ()).getState();
            sign.setLine(0, line1);
            sign.setLine(1, line2);
            sign.setLine(2, line3);
            sign.setLine(3, line4);
            sign.update(false, false);
        });
    }

    @Override
    protected String getPlayerName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public void stop() {
        if (this.runnableResult != null) ((BukkitTask) this.runnableResult).cancel();
    }

    private boolean isActive() {
        World world = Bukkit.getWorld(this.getWorld());
        if (world == null) {
            return false;
        }
        if (!world.isChunkLoaded(this.getX() >> 4, this.getZ() >> 4)) {
            return false;
        }
        Block block = world.getBlockAt(this.getX(), this.getY(), this.getZ());
        return block.getType().toString().endsWith("_SIGN");
    }
}
