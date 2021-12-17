package nl.lolmewn.stats.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldManager {

    private Map<UUID, Integer> worldMap = new HashMap<>();
    private Map<Integer, UUID> idMap = new HashMap<>();
    private Map<UUID, String> nameMap = new HashMap<>();

    public Optional<UUID> getWorld(int id) {
        return Optional.ofNullable(idMap.get(id));
    }

    public Optional<Integer> getWorld(UUID uuid) {
        return Optional.ofNullable(worldMap.get(uuid));
    }

    public String getName(UUID uuid) {
        return this.nameMap.get(uuid);
    }

    public String getName(int id) {
        return this.getWorld(id).map(this::getName).orElse(null);
    }

    public void setWorld(UUID uuid, int id, String name) {
        this.worldMap.put(uuid, id);
        this.idMap.put(id, uuid);
        this.nameMap.put(uuid, name);
    }

    public int addWorld(UUID uuid, String name) {
        return this.getWorld(uuid).orElseGet(() -> {
            int newId = this.idMap.keySet().stream().reduce(0, (a, b) -> a > b ? a : b) + 1;
            this.setWorld(uuid, newId, name);
            return newId;
        });
    }

    public void setWeather(UUID uid, boolean thundering) {
        throw new IllegalStateException("Can't save weather for world, not implemented");
    }
}
