package nl.lolmewn.stats.signs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.storage.StatTypeAdapter;
import nl.lolmewn.stats.storage.mysql.MySQLStorage;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class SignManager {

    private static SignManager signManager;
    public static SignManager getInstance() {
        return signManager;
    }

    private final Gson gson;
    private final Plugin plugin;
    private MySQLStorage storage;
    private Map<UUID, StatsSign> signs = new HashMap<>();

    private Logger log;

    public SignManager(Plugin plugin, Logger log, MySQLStorage storage) throws SQLException {
        signManager = this;
        this.plugin = plugin;
        this.log = log;
        this.storage = storage;
        this.gson = new GsonBuilder().registerTypeAdapter(Stat.class, new StatTypeAdapter()).create();
        this.loadSigns();
    }

    public void addSign(StatsSign sign) {
        this.signs.put(sign.getId(), sign);
        this.saveSign(sign);
    }

    public StatsSign getSign(UUID uuid) {
        return signs.get(uuid);
    }

    private void loadSigns() throws SQLException {
        try (Connection con = this.storage.getConnection()) {
            ResultSet set = con.createStatement().executeQuery("SELECT *,HEX(id) as uuid,HEX(world) AS worldUuid FROM stats_signs");
            while (set != null && set.next()) {
                Optional<UUID> uuid = Util.generateUUID(set.getString("uuid"));
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("worldUuid"));
                if (!uuid.isPresent()) {
                    this.log.severe("Could not load some sign, ID is not a valid uuid: " + set.getString("worldUuid"));
                    continue;
                }
                if (!worldUUID.isPresent()) {
                    this.log.severe("Could not load some sign, ID is not a valid uuid: " + set.getString("uuid"));
                    continue;
                }
                StatsSign sign = new BukkitStatsSign(plugin,
                        uuid.get(), set.getInt("x"), set.getInt("y"), set.getInt("z"),
                        worldUUID.get(), this.gson.fromJson(set.getString("spec"), StatsSignSpec.class));
                this.signs.put(uuid.get(), sign);
            }
        }
    }

    private void saveSign(StatsSign sign) {
        try (Connection con = this.storage.getConnection()) {
            PreparedStatement update = con.prepareStatement("UPDATE stats_signs SET world=UNHEX(?), x=?, y=?, z=?, spec=? WHERE id=UNHEX(?)");
            update.setString(1, sign.getWorld().toString().replace("-", ""));
            update.setInt(2, sign.getX());
            update.setInt(3, sign.getY());
            update.setInt(4, sign.getZ());
            update.setString(5, this.gson.toJson(sign.getSpec()));
            update.setString(6, sign.getId().toString().replace("-", ""));
            if (update.executeUpdate() == 0) {
                PreparedStatement insert = con.prepareStatement("INSERT INTO stats_signs VALUES (UNHEX(?), UNHEX(?), ?, ?, ?, ?)");
                insert.setString(1, sign.getId().toString().replace("-", ""));
                insert.setString(2, sign.getWorld().toString().replace("-", ""));
                insert.setInt(3, sign.getX());
                insert.setInt(4, sign.getY());
                insert.setInt(5, sign.getZ());
                insert.setString(6, this.gson.toJson(sign.getSpec()));
                insert.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.log.severe("Could not save sign :( It'll break after a server restart.");
        }
    }

    public Optional<StatsSign> getSignAt(UUID uid, int x, int y, int z) {
        return this.signs.values().stream()
                .filter(sign -> sign.getWorld().equals(uid) && sign.getX() == x && sign.getY() == y && sign.getZ() == z)
                .findAny();
    }

    public boolean removeSign(StatsSign statsSign) {
        statsSign.stop();
        this.signs.remove(statsSign.getId());
        try (Connection con = this.storage.getConnection()) {
            PreparedStatement delete = con.prepareStatement("DELETE FROM stats_signs WHERE id=UNHEX(?)");
            delete.setString(1, statsSign.getId().toString().replace("-", ""));
            delete.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            this.log.severe("Could not remove sign :( It'll still be there after a server restart.");
        }
        return false;
    }
}
