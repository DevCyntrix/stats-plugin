package nl.lolmewn.stats.storage;

import nl.lolmewn.stats.player.StatsPlayer;

import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class StorageManager {

    private static StorageManager instance;

    public StorageManager() {
        instance = this;
    }

    public static StorageManager getInstance() {
        return instance;
    }

    public Callable<StatsPlayer> loadPlayer(UUID uuid) {
        return () -> {
            StatsPlayer statsPlayer = new StatsPlayer(uuid);
            this.internalLoadPlayer(statsPlayer);
            return statsPlayer;
        };
    }

    public abstract void internalLoadPlayer(StatsPlayer player);
}
