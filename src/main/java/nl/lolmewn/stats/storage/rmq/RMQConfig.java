package nl.lolmewn.stats.storage.rmq;

public class RMQConfig {

    private final String username, password, host;
    private final int port;

    public RMQConfig(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
