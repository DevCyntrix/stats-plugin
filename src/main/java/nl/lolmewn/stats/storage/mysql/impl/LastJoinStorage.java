package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.storage.mysql.MySQLWorldManager;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

public class LastJoinStorage implements StatMySQLHandler {

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT * FROM stats_last_join t JOIN stats_players p ON p.id=t.player_id WHERE p.uuid=UNHEX(?) ORDER BY timestamp DESC LIMIT 1")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = MySQLWorldManager.getInstance().getWorld(set.getInt("world_id"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world id that is not existing: " + set.getInt("world_id"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("timestamp").getTime(), set.getTimestamp("timestamp").getTime(),
                        Util.of("world", worldUUID.get().toString())
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_last_join (player_id, world_id, `timestamp`) " +
                "VALUES (?, ?, ?)")) {
            st.setInt(1, player.getDbId());
            st.setInt(2, Util.getWorldId(entry.getMetadata().get("world").toString()).orElseThrow(IllegalStateException::new));
            st.setTimestamp(3, new Timestamp(entry.getTimestamp()));
            st.execute();
        }
    }
}
