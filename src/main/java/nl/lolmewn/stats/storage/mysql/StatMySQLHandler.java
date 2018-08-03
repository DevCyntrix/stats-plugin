package nl.lolmewn.stats.storage.mysql;

import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface StatMySQLHandler {
    void generateTables(Connection con) throws SQLException;

    Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException;

    void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException;
}
