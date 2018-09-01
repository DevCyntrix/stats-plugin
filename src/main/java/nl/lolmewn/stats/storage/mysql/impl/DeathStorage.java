package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

public class DeathStorage implements StatMySQLHandler {
    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `stats_death` (" +
                    "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `player` BINARY(16) NOT NULL," +
                    "  `world` BINARY(16) NOT NULL," +
                    "  `loc_x` INT NOT NULL," +
                    "  `loc_y` INT NOT NULL," +
                    "  `loc_z` INT NOT NULL," +
                    "  `cause` TEXT NOT NULL," +
                    "  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)," +
                    "  INDEX `uuid` (`player` ASC));");
        }
    }

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid FROM stats_death WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("timestamp").getTime(), 1,
                        Util.of("world", worldUUID.get().toString(),
                                "loc_x", set.getDouble("loc_x"),
                                "loc_y", set.getDouble("loc_y"),
                                "loc_z", set.getDouble("loc_z"),
                                "cause", set.getString("cause"))
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_death (player, world, loc_x, loc_y, loc_z, cause, timestamp) " +
                "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?, ?, ?)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setDouble(3, (Double) entry.getMetadata().get("loc_x"));
            st.setDouble(4, (Double) entry.getMetadata().get("loc_y"));
            st.setDouble(5, (Double) entry.getMetadata().get("loc_z"));
            st.setString(6, entry.getMetadata().get("cause").toString());
            st.setTimestamp(7, new Timestamp(entry.getTimestamp()));
            st.execute();
        }
    }
}
