package nl.lolmewn.stats.storage.mysql;

import nl.lolmewn.stats.player.MySQLStatsPlayer;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface StatMySQLHandler extends MySQLHandler {

    Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException;

    void storeEntry(Connection con, MySQLStatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException;
}
