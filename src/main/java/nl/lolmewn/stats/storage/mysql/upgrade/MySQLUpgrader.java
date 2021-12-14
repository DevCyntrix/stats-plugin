package nl.lolmewn.stats.storage.mysql.upgrade;

import nl.lolmewn.stats.Settings;
import nl.lolmewn.stats.storage.mysql.MySQLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MySQLUpgrader {

    private final Settings settings;
    private final int currentVersion;
    private final int latestVersion;

    private Logger log;

    public MySQLUpgrader(Logger log, Connection connection) throws SQLException, IOException {
        this.log = log;

        this.settings = new Settings(MySQLUpgrader.class.getResourceAsStream("/sql/db.properties"));
        this.currentVersion = getCurrentVersion(connection);
        this.latestVersion = getLatestVersion();
        if (currentVersion < latestVersion) {
            startUpgrade(connection);
        }
    }

    private void startUpgrade(Connection connection) throws SQLException, IOException {
        this.log.info("Upgrading MySQL from v" + this.currentVersion + " to v" + this.latestVersion);
        connection.setAutoCommit(false);
        connection.createStatement();
        for (int i = this.currentVersion + 1; i <= this.latestVersion; i++) {
            String sqlFile = this.settings.getString("sql_v" + i + "_file");
            if (sqlFile == null || "".equals(sqlFile)) {
                this.log.severe("Could not find upgrade file to version " + i);
                connection.rollback();
                return;
            }
            try {
                this.performUpgrade(connection, sqlFile);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
        connection.commit();
    }

    private void performUpgrade(Connection connection, String sqlFile) throws SQLException, IOException {
        InputStream in = MySQLUpgrader.class.getResourceAsStream(sqlFile);
        if (in == null) {
            throw new IllegalStateException("File could not be found: " + sqlFile);
        }
        this.log.info("Upgrading MySQL using " + sqlFile + "...");
        new ScriptRunner(this.log, connection, false).runScript(new InputStreamReader(in));
    }

    private int getCurrentVersion(Connection con) {
        try {
            if (MySQLUtil.tableExists(con, "stats_system")) {
                PreparedStatement st = con.prepareStatement("SELECT version FROM stats_system");
                ResultSet set = st.executeQuery();
                if (!set.next()) {
                    this.log.severe("[ERR] Could not find latest version of Stats database, assuming it was deleted...");
                    return 0;
                }
                return set.getInt("version");
            }
            if (!MySQLUtil.tableExists(con, "stats_block_place")) {
                // Probably there's nothing there yet.
                return 0;
            }
            if (MySQLUtil.columnExists(con, "stats_block_place", "timestamp")) {
                return 1; // Old version
            }
            throw new IllegalStateException("Unknown database version");
        } catch (SQLException ignored) {
            // Ignore the exception, table doesn't exist
        }
        return 0;
    }

    private int getLatestVersion() {
        return settings.getInt("latest_version");
    }

}
