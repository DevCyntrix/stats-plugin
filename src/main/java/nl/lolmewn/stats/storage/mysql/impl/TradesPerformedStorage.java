package nl.lolmewn.stats.storage.mysql.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.lolmewn.stats.SimpleItem;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

public class TradesPerformedStorage implements StatMySQLHandler {

    private final Gson gson = new Gson();

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid FROM stats_trades_performed WHERE player=UNHEX(?)")) {
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
                                "item", this.gson.fromJson(set.getString("item"), SimpleItem.class),
                                "price", this.gson.fromJson(set.getString("price"), new TypeToken<ArrayList<SimpleItem>>() {
                                }.getType()))
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO stats_trades_performed (player, world, item, price, timestamp) " +
                "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setString(3, this.gson.toJson(entry.getMetadata().get("item")));
            st.setString(4, this.gson.toJson(entry.getMetadata().get("price")));
            st.setTimestamp(5, new Timestamp(entry.getTimestamp()));
            st.execute();
        }
    }
}
