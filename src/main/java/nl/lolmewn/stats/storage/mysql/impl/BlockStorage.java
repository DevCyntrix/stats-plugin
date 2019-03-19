package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BlockStorage implements StatMySQLHandler {

    private final boolean breaking;
    private final String tableName;

    public BlockStorage(boolean breaking) {
        this.breaking = breaking;
        this.tableName = breaking ? "stats_block_break" : "stats_block_place";
    }

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid FROM " + this.tableName + " WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                if (breaking) {
                    entries.add(new StatTimeEntry(
                            set.getTimestamp("last_updated").getTime(), set.getDouble("amount"),
                            Util.of("world", worldUUID.get().toString(),
                                    "material", set.getString("material"),
                                    "tool", set.getString("tool"))
                    ));
                } else {
                    entries.add(new StatTimeEntry(
                            set.getTimestamp("last_updated").getTime(), set.getDouble("amount"),
                            Util.of("world", worldUUID.get().toString(),
                                    "material", set.getString("material"))
                    ));
                }
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        if (breaking) {
            try (PreparedStatement st = con.prepareStatement("INSERT INTO " + this.tableName + " (player, world, material, tool, amount) " +
                    "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
                inputCommon(player, entry, st);
                st.setObject(4, entry.getMetadata().get("tool"));
                st.setDouble(5, entry.getAmount());
                st.execute();
            }
        } else {
            try (PreparedStatement st = con.prepareStatement("INSERT INTO " + this.tableName + " (player, world, material, amount) " +
                    "VALUES (UNHEX(?), UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
                inputCommon(player, entry, st);
                st.setDouble(4, entry.getAmount());
                st.execute();
            }
        }
    }

    private void inputCommon(StatsPlayer player, StatTimeEntry entry, PreparedStatement st) throws SQLException {
        st.setString(1, player.getUuid().toString().replace("-", ""));
        st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
        st.setObject(3, entry.getMetadata().get("material"));
    }
}
