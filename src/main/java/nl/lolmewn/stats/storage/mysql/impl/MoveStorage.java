package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

@SuppressWarnings("SqlResolve")
public class MoveStorage implements StatMySQLHandler {

    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `stats_move` (" +
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
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid, amount, timestamp " +
                "FROM stats_move WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                double amount = set.getDouble("amount");
                entries.add(new StatTimeEntry(
                        set.getTimestamp("timestamp").getTime(), amount,
                        Map.of("world", worldUUID.get().toString(), "type", set.getString("type"))));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement update = con.prepareStatement("UPDATE stats_move SET amount=amount+? " +
                "WHERE player=UNHEX(?) AND world=UNHEX(?) AND type=?")) {
            update.setLong(1, (long) entry.getAmount());
            update.setString(2, player.getUuid().toString().replace("-", ""));
            update.setString(3, entry.getMetadata().get("world").toString().replace("-", ""));
            update.setObject(4, entry.getMetadata().get("type"));
            if (update.executeUpdate() == 0) {
                try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_move (player, world, amount, type) " +
                        "VALUES (UNHEX(?), UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
                    st.setString(1, player.getUuid().toString().replace("-", ""));
                    st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
                    st.setDouble(3, entry.getAmount());
                    st.setObject(4, entry.getMetadata().get("type"));
                    st.execute();
                }
            }
        }
    }
}
