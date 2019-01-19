package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.storage.mysql.MySQLHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneralDefaultStorage implements MySQLHandler {
    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `stats_players` (" +
                    "  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `uuid` BINARY(16) NOT NULL," +
                    "  `username` VARCHAR(16) NOT NULL DEFAULT 'Unknown'," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC));");
            st.execute("CREATE TABLE IF NOT EXISTS `stats_worlds` (" +
                    "  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `uuid` BINARY(16) NOT NULL," +
                    "  `name` TEXT NOT NULL," +
                    "  `raining` BOOLEAN NOT NULL," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC));");
            st.execute("CREATE TABLE IF NOT EXISTS `stats_system` (" +
                    " `version` INT UNSIGNED NOT NULL)");
        }
    }
}
