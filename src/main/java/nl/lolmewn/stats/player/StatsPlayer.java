package nl.lolmewn.stats.player;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class StatsPlayer {

    private final PublishSubject<StatsContainer> publishSubject = PublishSubject.create();
    private final UUID uuid;
    private final Map<Stat, StatsContainer> stats = new LinkedHashMap<>();

    public StatsPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public StatsContainer getStats(Stat stat) {
        if (!this.stats.containsKey(stat)) {
            StatsContainer container = new StatsContainer(stat);
            this.stats.put(stat, container);
            this.publishSubject.onNext(container);
        }
        return this.stats.get(stat);
    }

    public Collection<StatsContainer> getContainers() {
        return stats.values();
    }

    public Disposable subscribe(Consumer<StatsContainer> containerConsumer, Consumer<? super Throwable> handleError) {
        return this.publishSubject.subscribe(containerConsumer, handleError);
    }

    public Flowable<StatsContainer> getObservable() {
        return this.publishSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}
