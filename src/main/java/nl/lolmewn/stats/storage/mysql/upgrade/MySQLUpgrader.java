package nl.lolmewn.stats.storage.mysql.upgrade;

import nl.lolmewn.stats.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUpgrader {

    private final Settings settings;
    private final int currentVersion;
    private final int latestVersion;

    public MySQLUpgrader(Connection con) throws SQLException, IOException {
        this.settings = new Settings(MySQLUpgrader.class.getResourceAsStream("/sql/db.properties"));
        this.currentVersion = getCurrentVersion(con);
        this.latestVersion = getLatestVersion();
        if (currentVersion < latestVersion) {
            startUpgrade(con);
        }
    }

    private void startUpgrade(Connection con) throws SQLException, IOException {
        System.out.println("Upgrading MySQL from v" + this.currentVersion + " to v" + this.latestVersion);
        con.setAutoCommit(false);
        con.createStatement();
        for (int i = this.currentVersion + 1; i <= this.latestVersion; i++) {
            String sqlFile = this.settings.getString("sql_v" + i + "_file");
            if (sqlFile == null || "".equals(sqlFile)) {
                System.err.println("Could not find upgrade file to version " + i);
                con.rollback();
                return;
            }
            try {
                this.performUpgrade(con, sqlFile);
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
        con.commit();
    }

    private void performUpgrade(Connection con, String sqlFile) throws SQLException, IOException {
        InputStream in = MySQLUpgrader.class.getResourceAsStream(sqlFile);
        if (in == null) {
            throw new IllegalStateException("File could not be found: " + sqlFile);
        }
        System.out.println("Upgrading MySQL using " + sqlFile + "...");
        new ScriptRunner(con, false).runScript(new InputStreamReader(in));
    }

    private int getCurrentVersion(Connection con) {
        try {
            PreparedStatement st = con.prepareStatement("SELECT version FROM stats_system");
            ResultSet set = st.executeQuery();
            return set.next() ? set.getInt("version") : 0;
        } catch (SQLException ignored) {
            // Ignore the exception, table doesn't exist
        }
        return 0;
    }

    private int getLatestVersion() {
        return settings.getInt("latest_version");
    }

}
