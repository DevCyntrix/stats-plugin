package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

@SuppressWarnings("SqlResolve")
public class TypedStatStorage implements StatMySQLHandler {

    private final Stat stat;

    public TypedStatStorage(Stat stat) {
        this.stat = stat;
    }

    private String getTableName() {
        return "stats_" + this.stat.getName().toLowerCase().replace(" ", "_");
    }

    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (" +
                    "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `player` BINARY(16) NOT NULL," +
                    "  `world` BINARY(16) NOT NULL," +
                    "  `amount` DOUBLE NOT NULL," +
                    "  `type` TEXT NOT NULL," +
                    "  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)," +
                    "  INDEX `uuid_world` (`player` ASC));");
        }
    }

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT type,HEX(world) as world_uuid, amount, timestamp " +
                "FROM " + getTableName() + " WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("timestamp").getTime(), set.getDouble("amount"),
                        Util.of("world", worldUUID.get().toString(), "type", set.getString("type")
                        )));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO " + getTableName() + " (player, world, amount, type, timestamp) " +
                "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setDouble(3, entry.getAmount());
            st.setObject(4, entry.getMetadata().get("type"));
            st.setTimestamp(5, new Timestamp(entry.getTimestamp()));
            st.execute();
        }
    }
}
