package nl.lolmewn.stats.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {

    private static PlayerManager instance;
    private Map<UUID, StatsPlayer> players = new HashMap<>();

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public Optional<StatsPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    public void addPlayer(StatsPlayer player) {
        this.players.put(player.getUuid(), player);
    }
}
