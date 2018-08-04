package nl.lolmewn.stats.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.storage.StorageManager;
import nl.lolmewn.stats.storage.mysql.impl.BlockBreakStorage;
import nl.lolmewn.stats.storage.mysql.impl.GeneralPlayerStorage;
import nl.lolmewn.stats.storage.mysql.impl.PlaytimeStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySQLStorage extends StorageManager {

    private final HikariDataSource dataSource;
    private Map<Stat, StatMySQLHandler> handlers = new HashMap<>();

    public MySQLStorage(MySQLConfig config) throws SQLException {
        System.out.println("Starting MySQL Storage Engine...");
        HikariConfig hcnf = new HikariConfig();
        hcnf.setJdbcUrl(config.getJdbcUrl());
        hcnf.setUsername(config.getUsername());
        hcnf.setPassword(config.getPassword());
        this.dataSource = new HikariDataSource(hcnf);
        try {
            System.out.println("Checking MySQL connection...");
            checkConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Connection could not be established, please check the MySQL config", e);
        }

        this.registerHandlers();
        this.generateTables();
        System.out.println("MySQL ready to go!");
        PlayerManager.getInstance().subscribe(player -> player.subscribe(container ->
                container.subscribe(entry -> this.storeEntry(player, container, entry))));
    }

    private void generateTables() throws SQLException {
        try (Connection con = getConnection()) {
            for (StatMySQLHandler handler : this.handlers.values()) {
                handler.generateTables(con);
            }
        }
    }

    private void registerHandlers() {
        StatManager.getInstance().getStat("Playtime").ifPresent(stat -> this.handlers.put(stat, new PlaytimeStorage()));
        StatManager.getInstance().getStat("Blocks broken").ifPresent(stat -> this.handlers.put(stat, new BlockBreakStorage()));

        // Register all other stats to the default
        StatManager.getInstance().getStats().stream()
                .filter(stat -> !this.handlers.containsKey(stat))
                .forEach(stat -> this.handlers.put(stat, new GeneralPlayerStorage(stat)));
    }

    private void storeEntry(StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        if (this.handlers.containsKey(container.getStat())) {
            try (Connection con = this.getConnection()) {
                this.handlers.get(container.getStat()).storeEntry(con, player, container, entry);
            }
        }
    }

    public void checkConnection() throws SQLException {
        try (Connection con = getConnection()) {
            con.createStatement().execute("SELECT 1");
        }
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void internalLoadPlayer(StatsPlayer player) {
        try (Connection con = this.getConnection()) {
            for (Map.Entry<Stat, StatMySQLHandler> mapEntry : this.handlers.entrySet()) {
                for (StatTimeEntry entry : mapEntry.getValue().loadEntries(con, player.getUuid())) {
                    player.getStats(mapEntry.getKey()).addEntry(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
