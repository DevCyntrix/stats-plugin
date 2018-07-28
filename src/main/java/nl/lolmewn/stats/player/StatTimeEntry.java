package nl.lolmewn.stats.player;

import java.util.HashMap;
import java.util.Map;

public class StatTimeEntry {

    private final long timestamp;
    private final long amount;
    private final Map<String, Object> metadata = new HashMap<>();

    public StatTimeEntry(long timestamp, long amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public StatTimeEntry(long timestamp, long amount, Map<String, Object> metadata) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.metadata.putAll(metadata);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getAmount() {
        return amount;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
