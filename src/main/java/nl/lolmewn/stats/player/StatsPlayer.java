package nl.lolmewn.stats.player;

import nl.lolmewn.stats.stat.Stat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class StatsPlayer {

    private final UUID uuid;
    private final Map<Stat, StatsContainer> stats = new LinkedHashMap<>();

    public StatsPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public StatsContainer getStats(Stat stat) {
        if (!this.stats.containsKey(stat)) {
            this.stats.put(stat, new StatsContainer(stat));
        }
        return this.stats.get(stat);
    }
}
