package nl.lolmewn.stats;

import hu.akarnokd.rxjava2.debug.validator.RxJavaProtocolValidator;
import io.reactivex.schedulers.Schedulers;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.lolmewn.stats.converters.Stats2;
import nl.lolmewn.stats.converters.Stats3;
import nl.lolmewn.stats.global.GlobalStats;
import nl.lolmewn.stats.listener.Playtime;
import nl.lolmewn.stats.listener.bukkit.*;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.SimpleStatContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.storage.mysql.MySQLConfig;
import nl.lolmewn.stats.storage.mysql.MySQLStorage;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BukkitMain extends JavaPlugin {

    private GlobalStats globalStats;
    private MySQLStorage storage;

    @Override
    public void onEnable() {
        RxJavaProtocolValidator.enableAndChain();
        RxJavaProtocolValidator.setOnViolationHandler(Throwable::printStackTrace);
        Schedulers.start();

        this.checkConversion();

        super.getConfig().addDefault("server-id", UUID.randomUUID().toString());
        super.getConfig().options().copyDefaults(true);
        super.saveConfig();

        if (super.getConfig().getString("mysql.username", "username").equals("username")) {
            getLogger().info("Stats is not yet configured");
            getLogger().info("Stats has generated a config.yml");
            getLogger().info("Please configure Stats and then restart your server");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        SharedMain.registerStats();

        try {
            this.storage = new MySQLStorage(this.getMySQLConfig());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            this.getLogger().severe("Could not start MySQL, not starting plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BlockBreak(this);
        new BlockPlace(this);
        new PlayerDeath(this);
        new EntityDeath(this);
        new PlayerMove(this);
        new Playtime();
        new SimpleStatsListener(this);

        SharedMain.serverUuid = super.getConfig().getString("server-id");
        SharedMain.setDebug(super.getConfig().getBoolean("debug", false));
        if (!super.getConfig().getBoolean("global-stats-opt-out", false)) {
            this.globalStats = new GlobalStats(super.getConfig().getString("version", "v5.3"));
        }
        new Metrics(this);
    }

    private void checkConversion() {
        if (getConfig().contains("storage")
                || (getConfig().contains("convertFrom") && getConfig().getInt("convertFrom") == 3)) {
            new Stats3(this);
        } else if (getConfig().contains("snapshots")
                || (getConfig().contains("convertFrom") && getConfig().getInt("convertFrom") == 2)) {
            new Stats2(this.getConfig());
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
        if (this.globalStats != null) this.globalStats.shutdown();
        if (this.storage != null) this.storage.shutdown();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Your total stats");
        PlayerManager.getInstance().getPlayer(((Player) sender).getUniqueId()).subscribe(player ->
                sendStatistics(sender, player), err -> {
            sender.sendMessage(ChatColor.RED + "An Unknown error occurred!");
            System.out.println("Command error: " + err);
        });
        return true;
    }

    private void sendStatistics(CommandSender sender, StatsPlayer player) {
        StatManager.getInstance().getStats().forEach(stat -> {
            TextComponent statMessage = new TextComponent(stat.getName());
            statMessage.setColor(ChatColor.DARK_GREEN);
            statMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(stat.getDescription()).color(ChatColor.GOLD).create()));
            TextComponent colon = new TextComponent(": ");
            colon.setColor(ChatColor.RED);
            TextComponent statValue = new TextComponent(stat.format(player.getStats(stat).getTotal()));
            statValue.setColor(ChatColor.GOLD);
            statValue.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(getValuesFor("world", player.getStats(stat).getSimpleStatContainer()).entrySet().stream().map(
                            entry -> "In " + getServer().getWorld(UUID.fromString(entry.getKey())).getName() + ": " + stat.format(entry.getValue())
                    ).reduce((s, s2) -> s + "\n" + s2).orElse("No data recorded yet!")).create()
            ));
            sender.spigot().sendMessage(statMessage, colon, statValue);
        });
    }

    public Map<String, Double> getValuesFor(String metadataKey, SimpleStatContainer statContainer) {
        Map<String, Double> results = new TreeMap<>();
        statContainer.getValues().entrySet().stream()
                .filter(e -> e.getKey().containsKey(metadataKey))
                .forEach(e -> results.merge(e.getKey().get(metadataKey).toString(), e.getValue(), Double::sum));
        return results;
    }

    public MySQLStorage getMySQLStorage() {
        return this.storage;
    }
}
