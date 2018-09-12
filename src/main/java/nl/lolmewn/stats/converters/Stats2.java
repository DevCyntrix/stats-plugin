package nl.lolmewn.stats.converters;

import com.zaxxer.hikari.HikariDataSource;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;
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

    private final String[] oldTables = {"move", "death", "kill", "player", "players", "pvp", "block"};
    private final FileConfiguration conf;
    private final Logger logger;
    private final Map<String, UUID> worldUUIDMap = new HashMap<>();
    private final Map<String, Stat> columnStatMap = new HashMap<>();
    private Connection con;

    public Stats2(Logger logger, FileConfiguration config) throws SQLException, ParseException, InterruptedException, IOException {
        this.conf = config;
        this.logger = logger;
        Bukkit.getWorlds().forEach(world -> this.worldUUIDMap.put(world.getName(), world.getUID()));

        StatManager.getInstance().getStat("Playtime").ifPresent(stat -> columnStatMap.put("playtime", stat));
        StatManager.getInstance().getStat("Arrows shot").ifPresent(stat -> columnStatMap.put("arrows", stat));
        StatManager.getInstance().getStat("XP gained").ifPresent(stat -> columnStatMap.put("xpgained", stat));
        StatManager.getInstance().getStat("Times joined").ifPresent(stat -> columnStatMap.put("joins", stat));
        StatManager.getInstance().getStat("Fish caught").ifPresent(stat -> columnStatMap.put("fishcatched", stat));
        StatManager.getInstance().getStat("Damage taken").ifPresent(stat -> columnStatMap.put("damagetaken", stat));
        StatManager.getInstance().getStat("Times kicked").ifPresent(stat -> columnStatMap.put("timeskicked", stat));
        StatManager.getInstance().getStat("Tools broken").ifPresent(stat -> columnStatMap.put("toolsbroken", stat));
        StatManager.getInstance().getStat("Eggs thrown").ifPresent(stat -> columnStatMap.put("eggsthrown", stat));
        StatManager.getInstance().getStat("Items crafted").ifPresent(stat -> columnStatMap.put("itemscrafted", stat));
        StatManager.getInstance().getStat("Food consumed").ifPresent(stat -> columnStatMap.put("omnomnom", stat));
//        StatManager.getInstance().getStat("On fire").ifPresent(stat -> columnStatMap.put("onfire", stat));
        StatManager.getInstance().getStat("Words said").ifPresent(stat -> columnStatMap.put("wordssaid", stat));
        StatManager.getInstance().getStat("Commands performed").ifPresent(stat -> columnStatMap.put("commandsdone", stat));
        StatManager.getInstance().getStat("Last quit").ifPresent(stat -> columnStatMap.put("lastleave", stat));
        StatManager.getInstance().getStat("Last join").ifPresent(stat -> columnStatMap.put("lastjoin", stat));
//        StatManager.getInstance().getStat("Votes").ifPresent(stat -> columnStatMap.put("votes", stat));
        StatManager.getInstance().getStat("Teleports").ifPresent(stat -> columnStatMap.put("teleports", stat));
        StatManager.getInstance().getStat("Items picked up").ifPresent(stat -> columnStatMap.put("itempickups", stat));
        StatManager.getInstance().getStat("Beds entered").ifPresent(stat -> columnStatMap.put("bedenter", stat));
//        StatManager.getInstance().getStat("Buckets filled").ifPresent(stat -> columnStatMap.put("bucketfill", stat));
        StatManager.getInstance().getStat("Buckets emptied").ifPresent(stat -> columnStatMap.put("bucketempty", stat));
//        StatManager.getInstance().getStat("World changed").ifPresent(stat -> columnStatMap.put("worldchange", stat));
        StatManager.getInstance().getStat("Items dropped").ifPresent(stat -> columnStatMap.put("itemdrops", stat));
        StatManager.getInstance().getStat("Times sheared").ifPresent(stat -> columnStatMap.put("shear", stat));
        StatManager.getInstance().getStat("PVP kill streak").ifPresent(stat -> columnStatMap.put("pvpstreak", stat));
//        StatManager.getInstance().getStat("PVP top streak").ifPresent(stat -> columnStatMap.put("pvptopstreak", stat));
//        StatManager.getInstance().getStat("Money").ifPresent(stat -> columnStatMap.put("money", stat));
        StatManager.getInstance().getStat("Trades performed").ifPresent(stat -> columnStatMap.put("trades", stat));

        logger.info("Converting data from Stats 2 format to Stats5...");
        logger.info("Checking database connection...");
        this.connectToDatabase();
        logger.info("Database connection successful. Backing up the data...");
        this.makeDatabaseBackups();
        con.createStatement().execute("SET foreign_key_checks=0;");
        logger.info("Backups complete. Clearing corrupt data...");
        this.clearCorruptData();
        logger.info("Done. Checking and adding UUIDs...");
        this.addUUIDs();
        this.getWorldUUIDs();
        logger.info("Done. Now that all tables are set up nicely, we can start converting all data...");
        con.setAutoCommit(false);
        this.convertData();
        con.commit();
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

    private void getWorldUUIDs() throws SQLException {
        ResultSet set = con.createStatement().executeQuery("SELECT DISTINCT(world) FROM stats2_player");
        while (set.next()) {
            String worldName = set.getString("world");
            this.worldUUIDMap.computeIfAbsent(worldName, s -> UUID.randomUUID());
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
        convertBlockData();
        convertMoveData();
        convertDeathData();
        convertKillData();
        convertPVPData();
        convertPlayerData();
    }

    private void convertBlockData() {

    }

    private void convertPlayerData() throws SQLException {
        logger.info("Converting rest of player data...");
        int worlds = this.worldUUIDMap.size();
        StringBuilder replaceString = new StringBuilder();
        for (int i = 0; i < worlds; i++) {
            replaceString.append("REPLACE(");
        }
        String values = this.worldUUIDMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getKey().length() - o1.getKey().length())
                .map(entry -> ", '" + entry.getKey() + "', '" + entry.getValue().toString().replace("-", "") + "')")
                .reduce("", String::concat);
        ResultSet set = con.createStatement().executeQuery("DESCRIBE stats2_player");
        while (set.next()) {
            String columnName = set.getString(1);
            if (columnName.equalsIgnoreCase("counter") || columnName.equalsIgnoreCase("player_id")
                    || columnName.equalsIgnoreCase("world") || columnName.equalsIgnoreCase("lastjoin")
                    || columnName.equalsIgnoreCase("lastleave")) {
                continue; // Skip these always
            }
            if (!this.columnStatMap.containsKey(columnName)) {
                logger.info("Don't know how to convert column " + columnName);
                continue;
            }
            Stat stat = this.columnStatMap.get(columnName);
            if (stat.getMetaData().size() != 1) {
                // aw shit.
                logger.info("Ignoring " + stat.getName() + " for now.");
                continue;
            }
            String tableName = "stats_" + stat.getName().toLowerCase().replace(" ", "_");
            logger.info("Converting " + stat.getName() + " data...");
            con.createStatement().executeUpdate("INSERT INTO " + tableName + " (player, world, amount)" +
                    " SELECT UNHEX(REPLACE(uuid, '-', ''))," +
                    " UNHEX(" + replaceString.toString() + "world" + values + ")," +
                    " " + columnName +
                    " FROM stats2_player as d JOIN stats2_players AS p on d.player_id=p.player_id");
        }
    }

    private void convertPVPData() throws SQLException {
        logger.info("Converting PVP data...");
        ResultSet set = con.createStatement().executeQuery("SELECT p.uuid AS player, v.uuid AS victim, amount, world, weapon " +
                "FROM stats2_pvp AS d " +
                "JOIN stats2_players AS p ON d.player_id=p.player_id " +
                "JOIN stats2_players AS v ON d.killed=v.player_id;");
        PreparedStatement st = con.prepareStatement("INSERT INTO stats_pvp (player, world, victim, weaponType, weaponName) VALUE (" +
                "UNHEX(?), UNHEX(?), UNHEX(?), ?, ?)");
        int idx = 0;
        while (set.next()) {
            String worldName = set.getString("world");
            UUID worldUUID = this.worldUUIDMap.computeIfAbsent(worldName, s -> UUID.randomUUID());

            st.setString(1, set.getString("player").replace("-", ""));
            st.setString(2, worldUUID.toString().replace("-", ""));
            st.setString(3, set.getString("victim").replace("-", ""));
            st.setString(4, "minecraft:" + set.getString("weapon").toLowerCase().replace(" ", "_"));
            st.setString(5, set.getString("weapon"));
            for (int i = 0; i < set.getInt("amount"); i++) {
                st.addBatch();
                if (++idx % 1024 == 0) {
                    logger.info("Inserted " + idx + " rows...");
                    st.executeBatch();
                }
            }
        }
        int[] batch = st.executeBatch();
        logger.info("Inserted " + (batch.length + (idx - idx % 1024)) + " rows into stats_pvp");
    }

    private void convertMoveData() throws SQLException {
        logger.info("Converting Move data...");
        int worlds = this.worldUUIDMap.size();
        StringBuilder replaceString = new StringBuilder();
        for (int i = 0; i < worlds; i++) {
            replaceString.append("REPLACE(");
        }
        String values = this.worldUUIDMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getKey().length() - o1.getKey().length())
                .map(entry -> ", '" + entry.getKey() + "', '" + entry.getValue().toString().replace("-", "") + "')")
                .reduce("", String::concat);
        String query = "INSERT INTO stats_move (player, world, amount, type) " +
                "SELECT UNHEX(REPLACE(uuid, '-', ''))," +
                "    UNHEX(" + replaceString.toString() + " world " + values + ")," +
                "    distance," +
                "    CASE" +
                "        WHEN type = 0 THEN 'Walking'" +
                "        WHEN type = 1 THEN 'BOAT'" +
                "        WHEN type = 2 THEN 'MINECART'" +
                "        WHEN type = 3 THEN 'PIG'" +
                "        WHEN type = 4 THEN 'PIG'" +
                "        WHEN type = 5 THEN 'HORSE'" +
                "END" +
                "    FROM stats.stats2_move AS d" +
                "    JOIN stats.stats2_players AS p ON d.player_id=p.player_id;";
        logger.info("Move query: " + query);
        con.createStatement().executeUpdate(query);
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
                if (++idx % 1024 == 0) {
                    logger.info("Inserting " + idx + " rows...");
                    st.executeBatch();
                }
            }
        }
        int[] batch = st.executeBatch();
        logger.info("Inserted " + (batch.length + (idx - idx % 1024)) + " rows into stats_kill");
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
                if (++idx % 1024 == 0) {
                    logger.info("Inserted " + idx + " rows...");
                }
            }
        }
        int[] batch = st.executeBatch();
        logger.info("Inserted " + (batch.length + (idx - idx % 1024)) + " rows into stats_death");
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
