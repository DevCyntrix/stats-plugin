package nl.lolmewn.stats.storage.mysql;

import java.sql.Connection;
import java.sql.SQLException;

public interface MySQLHandler {

    void generateTables(Connection con) throws SQLException;

}
