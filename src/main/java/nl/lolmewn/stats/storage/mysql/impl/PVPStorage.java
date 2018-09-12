package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

public class PVPStorage implements StatMySQLHandler {
    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `stats_pvp` (" +
                    "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `player` BINARY(16) NOT NULL," +
                    "  `world` BINARY(16) NOT NULL," +
                    "  `victim` BINARY(16) NOT NULL," +
                    "  `weaponType` TEXT NOT NULL," +
                    "  `weaponName` TEXT NOT NULL," +
                    "  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)," +
                    "  INDEX `uuid` (`player` ASC));");
        }
    }

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid, HEX(victim) as victim " +
                "FROM stats_pvp WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                Optional<UUID> victimUUID = Util.generateUUID(set.getString("victim"));
                if (!victimUUID.isPresent()) {
                    throw new IllegalStateException("Found victim UUID that is not a UUID: " + set.getString("victim"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("timestamp").getTime(), 1,
                        Util.of("world", worldUUID.get().toString(),
                                "victim", victimUUID.get().toString(),
                                "weaponType", set.getString("weaponType"),
                                "weaponName", set.getString("weaponName"))
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_pvp (player, world, victim, weaponType, weaponName, timestamp) " +
                "VALUES (UNHEX(?), UNHEX(?), UNHEX(?), ?, ?, ?)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setString(3, entry.getMetadata().get("victim").toString());
            st.setString(4, entry.getMetadata().get("weaponType").toString());
            st.setString(5, entry.getMetadata().get("weaponName").toString());
            st.setTimestamp(6, new Timestamp(entry.getTimestamp()));
            st.execute();
        }
    }
}
