package nl.lolmewn.stats.player;

import io.reactivex.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

public class SimpleStatContainer {

    private final Disposable subscription;

    private final Map<String, Integer> values = new HashMap<>();

    public SimpleStatContainer(StatsContainer parent) {
        this.subscription = parent.subscribe(this::handleUpdate);
    }

    public void handleUpdate(StatTimeEntry entry) {

    }

    public void shutdown() {
        this.subscription.dispose();
    }
}
