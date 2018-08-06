package nl.lolmewn.stats;

import hu.akarnokd.rxjava2.debug.validator.RxJavaProtocolValidator;
import nl.lolmewn.stats.global.GlobalStats;
import nl.lolmewn.stats.listener.Playtime;
import nl.lolmewn.stats.listener.bukkit.BlockBreak;
import nl.lolmewn.stats.listener.bukkit.EntityDeath;
import nl.lolmewn.stats.listener.bukkit.PlayerDeath;
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

import java.sql.SQLException;
import java.util.UUID;

public class BukkitMain extends JavaPlugin {

    private GlobalStats globalStats;

    @Override
    public void onEnable() {
        RxJavaProtocolValidator.enableAndChain();
        RxJavaProtocolValidator.setOnViolationHandler(Throwable::printStackTrace);
        super.getConfig().addDefault("server-id", UUID.randomUUID().toString());
        super.getConfig().options().copyDefaults(true);
        super.saveConfig();

        SharedMain.registerStats();

        try {
            new MySQLStorage(this.getMySQLConfig());
        } catch (SQLException e) {
            e.printStackTrace();
            this.getLogger().severe("Could not start MySQL, not starting plugin.");
            return;
        }

        new PlayerJoin(this);
        new BlockBreak(this);
        new PlayerDeath(this);
        new EntityDeath(this);
        new Playtime();

        SharedMain.serverUuid = super.getConfig().getString("server-id");
        SharedMain.setDebug(super.getConfig().getBoolean("debug", false));
        if (!super.getConfig().getBoolean("global-stats-opt-out", false)) {
            this.globalStats = new GlobalStats();
        }
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
        sender.sendMessage(ChatColor.RED + "Your total stats");
        StatManager.getInstance().getStats().forEach(stat ->
                PlayerManager.getInstance().getPlayer(((Player) sender).getUniqueId()).subscribe(player ->
                                sender.sendMessage(
                                        ChatColor.DARK_GREEN + stat.getName() +
                                                ChatColor.RED + ": " +
                                                ChatColor.GOLD + player.getStats(stat).getTotal()),
                        err -> {
                            sender.sendMessage(ChatColor.RED + "An Unknown error occurred!");
                            System.out.println("Command error: " + err);
                        }));
        return true;
    }
}
