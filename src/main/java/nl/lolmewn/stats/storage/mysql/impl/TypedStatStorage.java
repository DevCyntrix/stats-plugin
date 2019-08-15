package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.storage.mysql.MySQLWorldManager;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT type, world_id, amount, last_updated " +
                "FROM " + getTableName() + " t JOIN stats_players p ON p.id=t.player_id WHERE p.uuid=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = MySQLWorldManager.getInstance().getWorld(set.getInt("world_id"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world id that is not existing: " + set.getInt("world_id"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("last_updated").getTime(), set.getDouble("amount"),
                        Util.of("world", worldUUID.get().toString(), "type", set.getString("type")
                        )));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO " + getTableName() + " (player_id, world_id, type, amount) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
            st.setInt(1, player.getDbId());
            st.setInt(2, Util.getWorldId(entry.getMetadata().get("world").toString()).orElseThrow(IllegalStateException::new));
            st.setObject(3, entry.getMetadata().get("type"));
            st.setDouble(4, entry.getAmount());
            st.execute();
        }
    }
}
