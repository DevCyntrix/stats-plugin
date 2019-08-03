package nl.lolmewn.stats.storage.mysql;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.storage.WorldManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLWorldManager extends WorldManager {

    private final MySQLStorage storage;

    public MySQLWorldManager(MySQLStorage storage) {
        this.storage = storage;
        this.loadWorlds();
    }

    private void loadWorlds() {
        try (Connection con = this.storage.getConnection()) {
            ResultSet set = con.createStatement().executeQuery("SELECT * FROM stats_worlds");
            while (set != null && set.next()) {
                super.setWorld(Util.generateUUID(set.getString("uuid")).orElseThrow(IllegalStateException::new),
                        set.getInt("id"), set.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWorld(UUID uuid, int id, String name) {
        super.setWorld(uuid, id, name);
        this.saveWorld(uuid, id, name);
    }

    private void saveWorld(UUID uuid, int id, String name) {
        try (Connection con = this.storage.getConnection()) {
            PreparedStatement st = con.prepareStatement("INSERT INTO");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
