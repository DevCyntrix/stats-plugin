package nl.lolmewn.stats.converters;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Stats2 {

    private final YamlConfiguration conf;

    public Stats2(FileConfiguration config) {
        this.conf = config;
        this.connectToDatabase();
        this.makeDatabaseBackups();
        this.convertData();
    }

    private void convertData() {
    }

    private void makeDatabaseBackups() {
    }

    private void connectToDatabase() {

    }

}
