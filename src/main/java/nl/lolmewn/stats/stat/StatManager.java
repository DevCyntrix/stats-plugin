package nl.lolmewn.stats.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StatManager {

    private static StatManager instance;
    private final Map<String, Stat> stats = new HashMap<>();

    public static StatManager getInstance() {
        if (instance == null) {
            instance = new StatManager();
        }
        return instance;
    }

    public Optional<Stat> getStat(String statName) {
        return Optional.ofNullable(this.stats.get(statName));
    }

    public void addStat(Stat stat) {
        this.stats.put(stat.getName(), stat);
    }

    public Collection<Stat> getStats() {
        return this.stats.values();
    }
}
