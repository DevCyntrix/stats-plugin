package nl.lolmewn.stats.player;

import java.util.HashMap;
import java.util.Map;

public class StatTimeEntry {

    private final long timestamp;
    private final double amount;
    private final Map<String, Object> metadata = new HashMap<>();

    public StatTimeEntry(long timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public StatTimeEntry(long timestamp, double amount, Map<String, Object> metadata) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.metadata.putAll(metadata);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
