package nl.lolmewn.stats.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldManager {

    private Map<UUID, Integer> worldMap = new HashMap<>();
    private Map<Integer, UUID> idMap = new HashMap<>();
    private Map<UUID, String> nameMap = new HashMap<>();

    public UUID getWorld(int id) {
        return idMap.get(id);
    }

    public int getWorld(UUID uuid) {
        return worldMap.get(uuid);
    }

    public String getName(UUID uuid) {
        return this.nameMap.get(uuid);
    }

    public String getName(int id) {
        return Optional.ofNullable(this.getWorld(id)).map(this::getName).orElse(null);
    }

    public void setWorld(UUID uuid, int id, String name) {
        this.worldMap.put(uuid, id);
        this.idMap.put(id, uuid);
        this.nameMap.put(uuid, name);
    }

    public int addWorld(UUID uuid, String name) {
        if (this.worldMap.containsKey(uuid)) return this.getWorld(uuid);
        int newId = this.idMap.keySet().stream().reduce(0, (a, b) -> a > b ? a : b) + 1;
        this.setWorld(uuid, newId, name);
        return newId;
    }
}
