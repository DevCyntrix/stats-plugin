package nl.lolmewn.stats.storage.mysql;

import java.sql.Connection;
import java.sql.SQLException;

class MySQLUpgradeChecker {

    MySQLUpgradeChecker(Connection con) throws SQLException {
        if (this.needsUpgrades()) {
            this.performUpgrades();
        }
    }

    private void performUpgrades() {

    }

    private boolean needsUpgrades() {
        return false;
    }
}
