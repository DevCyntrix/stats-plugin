package nl.lolmewn.stats.player;

import io.reactivex.Flowable;
import nl.lolmewn.stats.stat.Stat;
import org.reactivestreams.Subscriber;

import java.util.*;

public class StatsPlayer extends Flowable<StatsContainer> {

    private Set<Subscriber<? super StatsContainer>> subscribers = new HashSet<>();

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
            this.subscribers.forEach(sub -> sub.onNext(container));
        }
        return this.stats.get(stat);
    }

    public Collection<StatsContainer> getContainers() {
        return stats.values();
    }

    @Override
    protected void subscribeActual(Subscriber<? super StatsContainer> subscriber) {
        this.subscribers.add(subscriber);
    }
}
