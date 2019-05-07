package nl.lolmewn.stats.converters;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stats3 {

    private final Plugin plugin;
    private final FileConfiguration conf;
    private FileConfiguration mysqlConfig;

    private Connection con;
    private String prefix, host, user, pass, port, data;

    public Stats3(Plugin plugin) {
        plugin.getLogger().info("Starting conversion from Stats 3...");
        this.plugin = plugin;
        this.conf = plugin.getConfig();
        this.connectToDatabase();
        this.makeBackups();
        this.convertData();
    }

    private void convertData() {
    }

    private void makeBackups() {
        this.backupConfig();
        this.backupDatabase();
    }

    private void backupDatabase() {
        this.plugin.getLogger().info("Checking if mysqldump is available...");
        String[] cmd = {"mysqldump", "--user=" + this.user, "--password=" + this.pass, "--databases",
                this.data, "--host", this.host, "--port", this.port};
        try {
            File dest = new File(this.plugin.getDataFolder(), "mysqldump_stats3.sql");
            PrintStream ps = new PrintStream(dest);

            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            int character;
            while ((character = in.read()) != -1) {
                ps.write(character);
            }
            if (process.waitFor() != 0) {
                plugin.getLogger().info("mysqldump did not succeed... Making manual backups instead.");
            } else {
                ps.flush();
                plugin.getLogger().info("Wow, mysqldump succeeded! ");
                return;
            }
        } catch (IOException | InterruptedException e) {
            plugin.getLogger().info("Attempted to use mysqldump, did not work. Making manual backups instead...");
        }
        this.makeManualDatabaseBackup();
    }

    private void makeManualDatabaseBackup() {
        try {
            ResultSet set = this.con.createStatement().executeQuery("SHOW TABLES");
            System.out.println("Backing up all tables...");
            while (set.next()) {
                String originalName = set.getString(1);
                String destName = originalName + "_backup";
                System.out.println("Creating " + destName + "...");
                this.con.createStatement().execute("CREATE TABLE " + destName + " LIKE " + originalName);
                System.out.println("Copying data from " + originalName + " to " + destName);
                this.con.createStatement().execute("INSERT " + destName + " SELECT * FROM " + originalName);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not backup database, stopping conversion of data.", ex);
        }
    }

    private void backupConfig() {
        this.plugin.getLogger().info("Backing up your config files...");
        File originalConfig = new File(plugin.getDataFolder(), "config.yml");
        File originalMySQLConfig = new File(plugin.getDataFolder(), "mysql.yml");
        File destConfig = new File(plugin.getDataFolder(), "config_stats3.yml");
        File destMySQLConfig = new File(plugin.getDataFolder(), "mysql_stats3.yml");
        try {
            Files.copy(Path.of(originalConfig.toURI()), Path.of(destConfig.toURI()));
            Files.copy(Path.of(originalMySQLConfig.toURI()), Path.of(destMySQLConfig.toURI()));
        } catch (IOException e) {
            throw new IllegalStateException("Found Stats 3 but could not backup your files. Will not convert data.", e);
        }
    }

    private void connectToDatabase() {
        this.setupConfig();
        try {
            this.host = this.mysqlConfig.getString("host");
            this.port = this.mysqlConfig.getString("port");
            this.user = this.mysqlConfig.getString("user");
            this.pass = this.mysqlConfig.getString("pass");
            this.data = this.mysqlConfig.getString("database");
            this.prefix = this.mysqlConfig.getString("prefix");
            this.con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + data, user, pass);
            this.plugin.getLogger().info("Connection to DB successful.");
        } catch (SQLException ex) {
            throw new IllegalStateException("Found Stats 3 but database error occurred. Cannot convert data.", ex);
        }
    }

    private void setupConfig() {
        if (!"mysql".equalsIgnoreCase(conf.getString("storage"))) {
            throw new IllegalStateException("Found Stats 3, but storage config option was not 'mysql'. Cannot convert data.");
        }
        File file = new File(plugin.getDataFolder(), "mysql.yml");
        if (!file.exists()) {
            throw new IllegalStateException("Found Stats 3, but mysql.yml does not exist. Cannot convert data.");
        }
        this.mysqlConfig = YamlConfiguration.loadConfiguration(file);
        this.plugin.getLogger().info("Configuration files found successfully.");
    }
}
