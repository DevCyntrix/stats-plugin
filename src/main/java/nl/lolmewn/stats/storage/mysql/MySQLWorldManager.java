package nl.lolmewn.stats.storage.mysql;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.storage.WorldManager;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class MySQLWorldManager extends WorldManager {

    private static MySQLWorldManager instance;

    public MySQLWorldManager(MySQLStorage storage) {
        this.storage = storage;
        this.loadWorlds();
        instance = this;
    }

    private final MySQLStorage storage;

    public static MySQLWorldManager getInstance() {
        return instance;
    }

    private void loadWorlds() {
        try (Connection con = this.storage.getConnection()) {
            ResultSet set = con.createStatement().executeQuery("SELECT *,HEX(uuid) AS uuid_hexed FROM stats_worlds");
            while (set != null && set.next()) {
                super.setWorld(Util.generateUUID(set.getString("uuid_hexed")).orElseThrow(IllegalStateException::new),
                        set.getInt("id"), set.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWorld(UUID uuid, int id, String name) {
        super.setWorld(uuid, Optional.ofNullable(this.saveWorld(uuid, name)).orElse(id), name);
    }

    private Integer saveWorld(UUID uuid, String name) {
        try (Connection con = this.storage.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE stats_worlds SET name=? WHERE uuid=UNHEX(?)");
            st.setString(1, name);
            st.setString(2, uuid.toString().replace("-", ""));
            if (st.execute()) {
                return null;
            }
            PreparedStatement insert = con.prepareStatement("INSERT INTO stats_worlds (uuid, name, raining) VALUE (UNHEX(?), ?, false)",
                    Statement.RETURN_GENERATED_KEYS);
            insert.setString(1, uuid.toString().replace("-", ""));
            insert.setString(2, name);
            if (!insert.execute()) return null;
            return insert.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setWeather(UUID uid, boolean thundering) {
        try (Connection con = this.storage.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE stats_worlds SET raining=? WHERE uuid=UNHEX(?)");
            st.setBoolean(1, thundering);
            st.setString(2, uid.toString().replace("-", ""));
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
