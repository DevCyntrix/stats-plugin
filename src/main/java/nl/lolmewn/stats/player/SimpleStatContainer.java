package nl.lolmewn.stats.player;

import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleStatContainer {

    private final Disposable subscription;
    private final StatsContainer parent;
    private final Map<Map<String, Object>, Long> values = new HashMap<>();

    public SimpleStatContainer(StatsContainer parent) {
        this.parent = parent;
        this.subscription = parent.subscribe(this::handleUpdate, Util::handleError);
    }

    private void handleUpdate(StatTimeEntry entry) {
        Map<String, Object> collect = this.parent.getStat().getMetaData().stream().filter(StatMetaData::isGroupable)
                .map(StatMetaData::getId)
                .collect(Collectors.toMap(Function.identity(), o -> entry.getMetadata().get(o)));
        this.values.merge(collect, entry.getAmount(), Long::sum);
    }

    public Map<Map<String, Object>, Long> getValues() {
        return values;
    }

    public void shutdown() {
        this.subscription.dispose();
    }
}
