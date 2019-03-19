package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class KillStorage implements StatMySQLHandler {

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid FROM stats_kill WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("last_updated").getTime(), set.getDouble("amount"),
                        Util.of("world", worldUUID.get().toString(),
                                "victimType", set.getString("victimType"),
                                "victimName", set.getString("victimName"),
                                "weapon", set.getString("weapon"))
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_kill (player, world, victimType, victimName, weapon, amount) " +
                "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setString(3, entry.getMetadata().get("victimType").toString());
            st.setObject(4, entry.getMetadata().get("victimName"));
            st.setString(5, entry.getMetadata().get("weapon").toString());
            st.setDouble(6, entry.getAmount());
            st.execute();
        }
    }
}
