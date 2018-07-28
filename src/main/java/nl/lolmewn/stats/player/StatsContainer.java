package nl.lolmewn.stats.player;

import nl.lolmewn.stats.stat.Stat;

import java.util.ArrayList;
import java.util.List;

public class StatsContainer {

    private final Stat stat;
    private final List<StatTimeEntry> entries = new ArrayList<>();
    private long total;

    public StatsContainer(Stat stat) {
        this.stat = stat;
    }

    public void addEntry(StatTimeEntry entry) {
        this.entries.add(entry);
        this.total += entry.getAmount();
    }

    public List<StatTimeEntry> getEntries() {
        return entries;
    }

    public Stat getStat() {
        return stat;
    }

    public long getTotal() {
        return total;
    }
}
