package nl.lolmewn.stats.player;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import nl.lolmewn.stats.storage.StorageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final PublishSubject<StatsPlayer> publishSubject = PublishSubject.create();
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
        Observable<StatsPlayer> observable = Observable.create(emitter -> {
            StatsPlayer player = StorageManager.getInstance().loadPlayer(uuid).call();
            this.addPlayer(player);
            emitter.onNext(player);
            emitter.onComplete();
        });
        this.loadingPlayers.put(uuid, observable);
        return observable;
    }

    public void addPlayer(StatsPlayer player) {
        this.players.put(player.getUuid(), player);
        this.loadingPlayers.remove(player.getUuid());
        this.publishSubject.onNext(player);
    }

    public void removePlayer(StatsPlayer player) {
        this.players.remove(player.getUuid());
    }

    public Disposable subscribe(Consumer<StatsPlayer> playerConsumer, Consumer<? super Throwable> handleError) {
        return this.publishSubject.subscribe(playerConsumer, handleError);
    }

    public Flowable<StatsPlayer> getObservable() {
        return this.publishSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}
