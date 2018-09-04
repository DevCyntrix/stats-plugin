package nl.lolmewn.stats.converters;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Stats2 {

    private final String[] oldTables = {"move", "death", "kill", "player", "players", "pvp"};
    private final FileConfiguration conf;
    private DataSource dataSource;

    public Stats2(FileConfiguration config) throws SQLException {
        this.conf = config;
        this.connectToDatabase();
        this.makeDatabaseBackups();
        this.convertData();
    }

    private void convertData() {
    }

    private void makeDatabaseBackups() throws SQLException {
        String prefix = conf.getString("MySQL-Prefix");
        try (Connection con = this.dataSource.getConnection()) {
            for (String oldTable : this.oldTables) {
                con.createStatement().execute("RENAME TABLE " + prefix + oldTable + " TO backup_" + oldTable);
            }
        }
    }

    private void connectToDatabase() throws SQLException {
        HikariDataSource hds = new HikariDataSource();
        hds.setJdbcUrl(conf.getString("MySQL-User"));
        hds.setPassword(conf.getString("MySQL-Pass"));
        hds.setUsername("jdbc:mysql://" + conf.getString("MySQL-Host") + ":" +
                conf.getString("MySQL-Port") + "/" + conf.getString("MySQL-Database"));
        this.dataSource = hds.getDataSource();
        try (Connection con = this.dataSource.getConnection()) {
            con.createStatement().execute("SELECT 1"); // See if the connection works
        }
    }

}
