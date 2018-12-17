package nl.lolmewn.stats.storage.mysql.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.StatMySQLHandler;

import java.sql.*;
import java.util.*;

public class BlockStorage implements StatMySQLHandler {

    private final String tableName;

    public BlockStorage(boolean breaking) {
        this.tableName = breaking ? "stats_block_break" : "stats_block_place";
    }

    @Override
    public void generateTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (" +
                    "  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `player` BINARY(16) NOT NULL," +
                    "  `world` BINARY(16) NOT NULL," +
                    "  `material` VARCHAR(255) NOT NULL," +
                    "  `tool` VARCHAR(255) NOT NULL," +
                    "  `amount` DOUBLE NOT NULL," +
                    "  `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)," +
                    "  UNIQUE KEY `rest_UNIQUE` (`player`,`world`,`material`,`tool`)," +
                    "  INDEX `uuid` (`player` ASC));");
        }
    }

    @Override
    public Collection<StatTimeEntry> loadEntries(Connection con, UUID uuid) throws SQLException {
        List<StatTimeEntry> entries = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement("SELECT *,HEX(world) as world_uuid FROM " + this.tableName + " WHERE player=UNHEX(?)")) {
            st.setString(1, uuid.toString().replace("-", ""));
            ResultSet set = st.executeQuery();
            while (set != null && set.next()) {
                Optional<UUID> worldUUID = Util.generateUUID(set.getString("world_uuid"));
                if (!worldUUID.isPresent()) {
                    throw new IllegalStateException("Found world UUID that is not a UUID: " + set.getString("world_uuid"));
                }
                entries.add(new StatTimeEntry(
                        set.getTimestamp("last_updated").getTime(), set.getDouble("amount"),
                        Util.of("world", worldUUID.get().toString(),
                                "material", set.getString("material"),
                                "tool", set.getString("tool"))
                ));
            }
        }
        return entries;
    }

    @Override
    public void storeEntry(Connection con, StatsPlayer player, StatsContainer container, StatTimeEntry entry) throws SQLException {
        try (PreparedStatement st = con.prepareStatement("INSERT INTO " + this.tableName + " (player, world, material, tool, amount) " +
                "VALUES (UNHEX(?), UNHEX(?), ?, ?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)")) {
            st.setString(1, player.getUuid().toString().replace("-", ""));
            st.setString(2, entry.getMetadata().get("world").toString().replace("-", ""));
            st.setObject(3, entry.getMetadata().get("material"));
            st.setObject(4, entry.getMetadata().get("tool"));
            st.setDouble(5, entry.getAmount());
            st.execute();
        }
    }
}
