package nl.lolmewn.stats.player;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import nl.lolmewn.stats.storage.StorageManager;
import org.reactivestreams.Subscriber;

import java.util.*;

public class PlayerManager extends Flowable<StatsPlayer> {

    private Set<Subscriber<? super StatsPlayer>> subscribers = new HashSet<>();

    private static PlayerManager instance;
    private Map<UUID, StatsPlayer> players = new HashMap<>();
    private Map<UUID, Observable<StatsPlayer>> loadingPlayers = new HashMap<>();

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public Observable<StatsPlayer> getPlayer(UUID uuid) {
        if (this.players.containsKey(uuid)) {
            return Observable.just(this.players.get(uuid));
        }
        if (this.loadingPlayers.containsKey(uuid)) {
            return this.loadingPlayers.get(uuid);
        }
        Observable<StatsPlayer> observable = Observable.fromCallable(StorageManager.getInstance().loadPlayer(uuid))
                .map(statsPlayer -> {
                    this.addPlayer(statsPlayer);
                    return statsPlayer;
                });
        this.loadingPlayers.put(uuid, observable);
        return observable;
    }

    public void addPlayer(StatsPlayer player) {
        this.players.put(player.getUuid(), player);
        this.loadingPlayers.remove(player.getUuid());
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
