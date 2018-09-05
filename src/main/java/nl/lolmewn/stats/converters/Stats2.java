package nl.lolmewn.stats.converters;

import com.zaxxer.hikari.HikariDataSource;
import nl.lolmewn.stats.storage.mysql.MySQLConfig;
import nl.lolmewn.stats.storage.mysql.MySQLStorage;
import nl.lolmewn.stats.util.UUIDFetcher;
import nl.lolmewn.stats.util.UUIDHistoricalFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class Stats2 {

    private final String[] oldTables = {"move", "death", "kill", "player", "players", "pvp"};
    private final FileConfiguration conf;
    private final Logger logger;
    private final Map<String, UUID> worldUUIDMap = new HashMap<>();
    private Connection con;

    public Stats2(Logger logger, FileConfiguration config) throws SQLException, ParseException, InterruptedException, IOException {
        this.conf = config;
        this.logger = logger;
        Bukkit.getWorlds().forEach(world -> this.worldUUIDMap.put(world.getName(), world.getUID()));
        logger.info("Converting data from Stats 2 format to Stats5...");
        logger.info("Checking database connection...");
        this.connectToDatabase();
        logger.info("Database connection successful. Backing up the data...");
        this.makeDatabaseBackups();
        logger.info("Backups complete. Clearing corrupt data...");
        this.clearCorruptData();
        logger.info("Done. Checking and adding UUIDs...");
        this.addUUIDs();
        logger.info("Done. Now that all tables are set up nicely, we can start converting all data...");
        this.convertData();
        this.convertConfig();

        if (this.worldUUIDMap.size() != Bukkit.getWorlds().size()) {
            logger.info("Some world names seem to no longer exist, could not find the UUID for those worlds");
            logger.info("To still be able to convert data, random UUIDs were used.");
            logger.info("These worlds could not be found and have random UUIDs:");
            worldUUIDMap.entrySet().stream()
                    .filter(entry -> Bukkit.getWorld(entry.getKey()) == null)
                    .forEach(entry -> logger.info(entry.getKey() + ": " + entry.getValue().toString()));
        }
    }

    private void addUUIDs() throws SQLException, ParseException, InterruptedException, IOException {
        ResultSet set = con.createStatement().executeQuery("SELECT `name`,`firstjoin` FROM stats2_players WHERE uuid IS NULL");
        Map<String, Long> namesToConvert = new HashMap<>();
        while (set != null && set.next()) {
            namesToConvert.put(set.getString(1), set.getTimestamp(2).getTime() / 1000);
        }
        logger.info("Found " + namesToConvert.size() + " names to get UUIDs for...");
        Map<String, UUID> uuidMap = new UUIDFetcher(new ArrayList<>(namesToConvert.keySet())).call();
        logger.info(uuidMap.size() + " successfully fetched");
        if (uuidMap.size() < namesToConvert.size()) {
            uuidMap.keySet().forEach(namesToConvert::remove);
            logger.info("Fetching the remaining " + namesToConvert.size() + " UUIDs, this might take a while...");
            Map<String, UUID> call = new UUIDHistoricalFetcher(namesToConvert).call();
            call.forEach(uuidMap::put);
            call.forEach((s, uuid) -> namesToConvert.remove(s));
        }
        if (!namesToConvert.isEmpty()) {
            // Well... you can't save all of them :(
            logger.info("Could not get UUID for " + namesToConvert.size() + " players, deleting their data (I'm sorry :S)...");
            logger.info("Their data will still be present in the backups made previously though!");
            for (String name : namesToConvert.keySet()) {
                logger.info("Deleting data for " + name + "...");
                PreparedStatement st = con.prepareStatement("SELECT player_id FROM stats2_players WHERE name=?");
                st.setString(1, name);
                ResultSet idSet = st.executeQuery();
                if (idSet.next()) {
                    int id = idSet.getInt(1);
                    for (String oldTable : this.oldTables) {
                        con.createStatement().execute("DELETE FROM stats2_" + oldTable + " WHERE player_id=" + id);
                    }
                }
            }
        }
        PreparedStatement st = con.prepareStatement("UPDATE stats2_players SET uuid=? WHERE `name`=?");
        for (Map.Entry<String, UUID> entry : uuidMap.entrySet()) {
            st.setString(1, entry.getValue().toString());
            st.setString(2, entry.getKey());
            st.addBatch();
        }
        st.executeBatch();
    }

    private void clearCorruptData() throws SQLException {
        logger.info("Clearing null or invalid data...");
        for (String oldTable : this.oldTables) {
            logger.info("Deleted " +
                    con.createStatement().executeUpdate("DELETE FROM stats2_" + oldTable + " WHERE player_id IS NULL")
                    + " invalid rows from stats2_" + oldTable);
        }
    }

    private void convertConfig() {
    }

    private void convertData() throws SQLException {
        // For easy generation of new tables
        logger.info("Generating new tables...");
        new MySQLStorage(new MySQLConfig(getJDBCURL(), conf.getString("MySQL-User"), conf.getString("MySQL-Pass"))).shutdown();
        convertDeathData();
        convertKillData();
        convertMoveData();
    }

    private void convertMoveData() {

    }

    private void convertKillData() throws SQLException {
        logger.info("Converting Kill data...");
        ResultSet set = con.createStatement().executeQuery("SELECT uuid, amount, world, type FROM stats2_kill AS d " +
                "JOIN stats2_players AS p ON d.player_id=p.player_id;");
        PreparedStatement st = con.prepareStatement("INSERT INTO stats_kill (player, world, victimType, victimName, weapon) VALUE (" +
                "UNHEX(?), UNHEX(?), ?, ?, ?)");
        int idx = 0;
        while (set.next()) {
            String worldName = set.getString("world");
            UUID worldUUID = this.worldUUIDMap.computeIfAbsent(worldName, s -> UUID.randomUUID());

            st.setString(1, set.getString("uuid").replace("-", ""));
            st.setString(2, worldUUID.toString().replace("-", ""));
            st.setString(3, set.getString("type").toUpperCase());
            st.setString(4, set.getString("type").toUpperCase());
            st.setString(5, "Unknown");
            for (int i = 0; i < set.getInt("amount"); i++) {
                st.addBatch();
                if (++idx % 100 == 0) {
                    logger.info("Inserted " + idx + " rows...");
                }
            }
        }
        int[] batch = st.executeBatch();
        logger.info("Inserted " + batch.length + " rows into stats_kill");
    }

    private void convertDeathData() throws SQLException {
        logger.info("Converting Death data...");
        ResultSet set = con.createStatement().executeQuery("SELECT uuid, amount, world, cause FROM stats2_death AS d " +
                "JOIN stats2_players AS p ON d.player_id=p.player_id;");
        PreparedStatement st = con.prepareStatement("INSERT INTO stats_death (player, world, loc_x, loc_y, loc_z, cause) VALUE (" +
                "UNHEX(?), UNHEX(?), ?, ?, ?, ?)");
        int idx = 0;
        while (set.next()) {
            String worldName = set.getString("world");
            UUID worldUUID = this.worldUUIDMap.computeIfAbsent(worldName, s -> UUID.randomUUID());
            Location spawn = Optional.ofNullable(Bukkit.getWorld(worldUUID)).map(World::getSpawnLocation).orElse(new Location(null, 0, 0, 0));

            st.setString(1, set.getString("uuid").replace("-", ""));
            st.setString(2, worldUUID.toString().replace("-", ""));
            st.setInt(3, spawn.getBlockX());
            st.setInt(4, spawn.getBlockY());
            st.setInt(5, spawn.getBlockZ());
            st.setString(6, set.getString("cause").toUpperCase());
            for (int i = 0; i < set.getInt("amount"); i++) {
                st.addBatch();
                if (++idx % 100 == 0) {
                    logger.info("Inserted " + idx + " rows...");
                }
            }
        }
        int[] batch = st.executeBatch();
        logger.info("Inserted " + batch.length + " rows into stats_death");
    }

    private void makeDatabaseBackups() throws SQLException {
        logger.info("Renaming all tables to backup_<table>...");
        String prefix = conf.getString("MySQL-Prefix");
        for (String oldTable : this.oldTables) {
            this.con.createStatement().execute("RENAME TABLE " + prefix + oldTable + " TO backup_" + oldTable);
        }
        logger.info("Creating new stats2_<table> tables...");
        this.con.createStatement().execute("CREATE TABLE `stats2_death` (" +
                "  `counter` int(11) NOT NULL AUTO_INCREMENT," +
                "  `player_id` int(11) DEFAULT NULL," +
                "  `world` varchar(255) DEFAULT 'main'," +
                "  `cause` varchar(32) NOT NULL," +
                "  `amount` int(11) NOT NULL," +
                "  `entity` tinyint(1) NOT NULL," +
                "  PRIMARY KEY (`counter`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        this.con.createStatement().execute("CREATE TABLE `stats2_kill` (" +
                "  `counter` int(11) NOT NULL AUTO_INCREMENT," +
                "  `player_id` int(11) DEFAULT NULL," +
                "  `world` varchar(255) DEFAULT 'main'," +
                "  `type` varchar(32) NOT NULL," +
                "  `amount` int(11) NOT NULL," +
                "  PRIMARY KEY (`counter`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        this.con.createStatement().execute("CREATE TABLE `stats2_move` (" +
                "  `counter` int(11) NOT NULL AUTO_INCREMENT," +
                "  `player_id` int(11) DEFAULT NULL," +
                "  `world` varchar(255) DEFAULT 'main'," +
                "  `type` tinyint(4) NOT NULL," +
                "  `distance` double NOT NULL," +
                "  PRIMARY KEY (`counter`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        this.con.createStatement().execute("CREATE TABLE `stats2_player` (" +
                "  `counter` int(11) NOT NULL AUTO_INCREMENT," +
                "  `player_id` int(11) DEFAULT NULL," +
                "  `world` varchar(255) DEFAULT 'main'," +
                "  `playtime` int(11) NOT NULL DEFAULT '0'," +
                "  `arrows` int(11) DEFAULT '0'," +
                "  `xpgained` int(11) DEFAULT '0'," +
                "  `joins` int(11) DEFAULT '0'," +
                "  `fishcatched` int(11) DEFAULT NULL," +
                "  `damagetaken` int(11) DEFAULT '0'," +
                "  `timeskicked` int(11) DEFAULT '0'," +
                "  `toolsbroken` int(11) DEFAULT '0'," +
                "  `eggsthrown` int(11) DEFAULT '0'," +
                "  `itemscrafted` int(11) DEFAULT '0'," +
                "  `omnomnom` int(11) DEFAULT '0'," +
                "  `onfire` int(11) DEFAULT '0'," +
                "  `wordssaid` int(11) DEFAULT '0'," +
                "  `commandsdone` int(11) DEFAULT '0'," +
                "  `lastleave` timestamp NULL DEFAULT '0000-00-00 00:00:00'," +
                "  `lastjoin` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'," +
                "  `votes` int(11) DEFAULT '0'," +
                "  `teleports` int(11) DEFAULT '0'," +
                "  `itempickups` int(11) DEFAULT '0'," +
                "  `bedenter` int(11) DEFAULT '0'," +
                "  `bucketfill` int(11) DEFAULT '0'," +
                "  `bucketempty` int(11) DEFAULT '0'," +
                "  `worldchange` int(11) DEFAULT '0'," +
                "  `itemdrops` int(11) DEFAULT '0'," +
                "  `shear` int(11) DEFAULT '0'," +
                "  `pvpstreak` int(11) DEFAULT '0'," +
                "  `pvptopstreak` int(11) DEFAULT '0'," +
                "  `money` double DEFAULT '0'," +
                "  `trades` int(11) DEFAULT '0'," +
                "  PRIMARY KEY (`counter`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        this.con.createStatement().execute("CREATE TABLE `stats2_players` (" +
                "  `player_id` int(11) NOT NULL," +
                "  `UUID` varchar(255) DEFAULT NULL," +
                "  `name` varchar(255) DEFAULT NULL," +
                "  `Firstjoin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`player_id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        this.con.createStatement().execute("CREATE TABLE `stats2_pvp` (" +
                "  `counter` int(11) NOT NULL AUTO_INCREMENT," +
                "  `player_id` int(11) NOT NULL," +
                "  `world` varchar(255) NOT NULL DEFAULT 'main'," +
                "  `killed` int(11) DEFAULT NULL," +
                "  `weapon` varchar(255) DEFAULT NULL," +
                "  `amount` int(11) DEFAULT NULL," +
                "  PRIMARY KEY (`counter`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        logger.info("Copying data over...");
        for (String oldTable : this.oldTables) {
            logger.info("Copying from backup_" + oldTable + " to stats2_" + oldTable + "...");
            this.con.createStatement().execute("INSERT INTO stats2_" + oldTable + " SELECT * FROM backup_" + oldTable);
        }
    }

    private void connectToDatabase() throws SQLException {
        HikariDataSource hds = new HikariDataSource();
        hds.setJdbcUrl(getJDBCURL());
        hds.setPassword(conf.getString("MySQL-Pass"));
        hds.setUsername(conf.getString("MySQL-User"));
        this.con = hds.getConnection();
        this.con.createStatement().execute("SELECT 1"); // See if the connection works
    }

    private String getJDBCURL() {
        return "jdbc:mysql://" + conf.getString("MySQL-Host") + ":" +
                conf.getString("MySQL-Port") + "/" + conf.getString("MySQL-Database");
    }

}
