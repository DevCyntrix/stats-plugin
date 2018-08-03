package nl.lolmewn.stats.storage.mysql;

public class MySQLConfig {

    private final String jdbcUrl, username;
    private final transient String password;

    public MySQLConfig(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }
}
