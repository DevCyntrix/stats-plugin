package nl.lolmewn.stats.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.storage.StorageManager;
import nl.lolmewn.stats.storage.mysql.impl.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySQLStorage extends StorageManager {

    private final HikariDataSource dataSource;
    private Map<Stat, StatMySQLHandler> handlers = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

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
        this.checkTableUpgrades();
        System.out.println("MySQL ready to go!");
        this.disposable.add(PlayerManager.getInstance().subscribe(this.getPlayerConsumer(), Util::handleError));
    }

    private void checkTableUpgrades() {
        try (Connection con = this.getConnection()) {
            new MySQLUpgradeChecker(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Consumer<StatsPlayer> getPlayerConsumer() {
        return player -> {
            System.out.println("New player triggered: " + player.getUuid().toString());
            player.getContainers().forEach(cont -> // Listen to updates of already-in-place containers
                    this.disposable.add(cont.subscribe(this.getStatTimeEntryConsumer(player, cont), Util::handleError)));
            this.disposable.add(player.subscribe(this.getContainerConsumer(player), Util::handleError)); // Listen to new containers
        };
    }

    private Consumer<StatsContainer> getContainerConsumer(StatsPlayer player) {
        return statsContainer ->
                this.disposable.add(statsContainer.getPublishSubject().subscribe(
                        this.getStatTimeEntryConsumer(player, statsContainer), Util::handleError)
                );
    }

    private Consumer<StatTimeEntry> getStatTimeEntryConsumer(StatsPlayer player, StatsContainer statsContainer) {
        return statTimeEntry -> this.disposable.add(Flowable.just(statTimeEntry).subscribeOn(Schedulers.io())
                .subscribe((entry) -> this.storeEntry(player, statsContainer, entry), Util::handleError));
//        return statTimeEntry -> this.storeEntry(player, statsContainer, statTimeEntry);
    }
    private void generateTables() throws SQLException {
        try (Connection con = getConnection()) {
            for (StatMySQLHandler handler : this.handlers.values()) {
                handler.generateTables(con);
            }
        }
    }

    private void registerHandlers() {
//        StatManager.getInstance().getStat("Playtime").ifPresent(stat -> this.handlers.put(stat, new PlaytimeStorage()));
        StatManager.getInstance().getStat("Blocks broken").ifPresent(stat -> this.handlers.put(stat, new BlockStorage(true)));
        StatManager.getInstance().getStat("Blocks placed").ifPresent(stat -> this.handlers.put(stat, new BlockStorage(false)));
        StatManager.getInstance().getStat("Deaths").ifPresent(stat -> this.handlers.put(stat, new DeathStorage()));
        StatManager.getInstance().getStat("Kills").ifPresent(stat -> this.handlers.put(stat, new KillStorage()));
        StatManager.getInstance().getStat("Last join").ifPresent(stat -> this.handlers.put(stat, new LastJoinStorage()));
        StatManager.getInstance().getStat("Last quit").ifPresent(stat -> this.handlers.put(stat, new LastQuitStorage()));
        StatManager.getInstance().getStat("Trades performed").ifPresent(stat -> this.handlers.put(stat, new TradesPerformedStorage()));
//        StatManager.getInstance().getStat("Move").ifPresent(stat -> this.handlers.put(stat, new MoveStorage()));

        // Register all other stats to the default
        StatManager.getInstance().getStats().stream()
                .filter(stat -> !this.handlers.containsKey(stat))
                .forEach(stat -> {
                    if (stat.getMetaData().stream().anyMatch(meta -> meta.getId().equalsIgnoreCase("type"))) {
                        this.handlers.put(stat, new TypedStatStorage(stat));
                    } else {
                        this.handlers.put(stat, new GeneralStatStorage(stat));
                    }
                });
    }

    private void storeEntry(StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        if (this.handlers.containsKey(container.getStat())) {
            try (Connection con = this.getConnection()) {
                try {
                    this.handlers.get(container.getStat()).storeEntry(con, player, container, entry);
                } catch (SQLException ex) {
                    System.out.printf("Error occurred when trying to save %s data for player %s, full error below.",
                            container.getStat().getName(), player.getUuid().toString());
                    throw ex; // rethrow
                }
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
