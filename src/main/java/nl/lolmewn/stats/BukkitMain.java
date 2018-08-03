package nl.lolmewn.stats;

import nl.lolmewn.stats.global.GlobalStats;
import nl.lolmewn.stats.listener.Playtime;
import nl.lolmewn.stats.listener.bukkit.BlockBreak;
import nl.lolmewn.stats.listener.bukkit.PlayerJoin;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.storage.mysql.MySQLConfig;
import nl.lolmewn.stats.storage.mysql.MySQLStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {

    private GlobalStats globalStats;

    @Override
    public void onEnable() {
        super.getConfig().options().copyDefaults(true);
        super.saveConfig();

        SharedMain.registerStats();

        new MySQLStorage(this.getMySQLConfig());

        new PlayerJoin(this);
        new BlockBreak(this);
        new Playtime();

        this.globalStats = new GlobalStats();
        SharedMain.serverUuid = this.getServer().getServerId();
    }

    private MySQLConfig getMySQLConfig() {
        return new MySQLConfig(
                this.getConfig().getString("mysql.url"),
                this.getConfig().getString("mysql.username"),
                this.getConfig().getString("mysql.password"));
    }

    @Override
    public void onDisable() {
        this.globalStats.shutdown();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only");
            return true;
        }
        StatManager.getInstance().getStats().forEach(stat ->
                PlayerManager.getInstance().getPlayer(((Player) sender).getUniqueId()).subscribe(player ->
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + stat.getName() + ": " + ChatColor.BLUE +
                                player.getStats(stat).getTotal())));
        return false;
    }
}
