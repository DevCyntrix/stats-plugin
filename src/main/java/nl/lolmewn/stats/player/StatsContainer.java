package nl.lolmewn.stats.player;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import nl.lolmewn.stats.stat.Stat;

import java.util.ArrayList;
import java.util.List;

public class StatsContainer {

    private final PublishSubject<StatTimeEntry> publishSubject = PublishSubject.create();

    private final Stat stat;
    private final List<StatTimeEntry> entries = new ArrayList<>();
    private double total;
    private final SimpleStatContainer simpleStatContainer;

    public StatsContainer(Stat stat) {
        this.stat = stat;
        this.simpleStatContainer = new SimpleStatContainer(this);
    }

    public void addEntry(StatTimeEntry entry) {
        this.entries.add(entry);
        this.total += entry.getAmount();
        this.publishSubject.onNext(entry);
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

    public double getTotal() {
        return total;
    }

    public Disposable subscribe(Consumer<StatTimeEntry> timeEntryConsumer, Consumer<? super Throwable> handleError) {
        return this.publishSubject.subscribe(timeEntryConsumer, handleError);
    }

    public Flowable<StatTimeEntry> getPublishSubject() {
        return publishSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void reset() {
        this.entries.clear();
        this.total = 0;
        this.simpleStatContainer.reset();
    }

    public void resetWhere(String key, Object value) {
        this.entries.removeIf(entry -> entry.getMetadata().containsKey(key) && entry.getMetadata().get(key).equals(value));
        this.total = this.entries.stream().mapToDouble(StatTimeEntry::getAmount).sum();
        this.simpleStatContainer.removeWhere(key, value);
    }
}
