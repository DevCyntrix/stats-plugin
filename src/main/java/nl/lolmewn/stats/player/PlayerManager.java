package nl.lolmewn.stats.player;

import io.reactivex.Flowable;
import org.reactivestreams.Subscriber;

import java.util.*;

public class PlayerManager extends Flowable<StatsPlayer> {

    private Set<Subscriber<? super StatsPlayer>> subscribers = new HashSet<>();

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
        this.subscribers.forEach(sub -> sub.onNext(player));
    }

    public void removePlayer(StatsPlayer player) {
        this.players.remove(player.getUuid());
    }

    @Override
    protected void subscribeActual(Subscriber<? super StatsPlayer> subscriber) {
        this.subscribers.add(subscriber);
    }
}
