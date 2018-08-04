package nl.lolmewn.stats.player;

import io.reactivex.Flowable;
import nl.lolmewn.stats.stat.Stat;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatsContainer extends Flowable<StatTimeEntry> {

    private final Set<Subscriber<? super StatTimeEntry>> subscribers = new HashSet<Subscriber<? super StatTimeEntry>>();

    private final Stat stat;
    private final List<StatTimeEntry> entries = new ArrayList<>();
    private long total;
    private final SimpleStatContainer simpleStatContainer;

    public StatsContainer(Stat stat) {
        this.stat = stat;
        this.simpleStatContainer = new SimpleStatContainer(this);
    }

    public void addEntry(StatTimeEntry entry) {
        this.entries.add(entry);
        this.total += entry.getAmount();
        this.subscribers.forEach(sub -> sub.onNext(entry));
    }

    public SimpleStatContainer getSimpleStatContainer() {
        return simpleStatContainer;
    }

    public List<StatTimeEntry> getEntries() {
        return entries;
    }

    public Stat getStat() {
        return stat;
    }

    public long getTotal() {
        return total;
    }

    @Override
    protected void subscribeActual(Subscriber<? super StatTimeEntry> subscriber) {
        this.subscribers.add(subscriber);
    }
}
