package nl.lolmewn.stats.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUtil {

    public static boolean tableExists(Connection con, String table) throws SQLException {
        PreparedStatement st = con.prepareStatement("SHOW TABLES LIKE " + table);
        ResultSet set = st.executeQuery();
        boolean result = set.next();
        st.close();
        return result;
    }

    public static boolean columnExists(Connection con, String table, String column) throws SQLException {
        PreparedStatement st = con.prepareStatement("SHOW COLUMNS FROM " + table + " LIKE " + column);
        ResultSet set = st.executeQuery();
        boolean result = set.next();
        st.close();
        return result;
    }
}
