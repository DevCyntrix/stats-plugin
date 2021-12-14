package nl.lolmewn.stats;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.storage.mysql.MySQLWorldManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    public static final Pattern PATTERN_UUID = Pattern.compile("^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_HEXED_UUID = Pattern.compile("^([a-z0-9]{8})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{12})$", Pattern.CASE_INSENSITIVE);

    protected static Logger log;

    public static Optional<Integer> getWorldId(String uuid) {
        return generateUUID(uuid).flatMap(val -> MySQLWorldManager.getInstance().getWorld(val));
    }

    public static Optional<UUID> generateUUID(String hex) {
        try {
            if (!PATTERN_UUID.matcher(hex).matches()) {
                Matcher matcher = PATTERN_HEXED_UUID.matcher(hex);
                if (matcher.matches()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        if (i != 1) {
                            sb.append("-");
                        }
                        sb.append(matcher.group(i));
                    }
                    return Optional.of(UUID.fromString(sb.toString()));
                }
            } else {
                return Optional.of(UUID.fromString(hex));
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    public static void handleError(Throwable throwable) {
        log.log(Level.SEVERE, "Error occurred, see stacktrace", throwable);
        // throwable.printStackTrace();
    }

    public static Disposable statUpdate(TriConsumer<StatsPlayer, StatsContainer, StatTimeEntry> consumer) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(PlayerManager.getInstance().getObservable()
                .subscribe(player -> {
                    disposable.add(player.getObservable()
                            .subscribe(container -> disposable.add(container.getPublishSubject()
                                    .subscribe(entry -> consumer.accept(player, container, entry), Util::handleError)), Util::handleError));
                    player.getContainers().forEach(cont -> // Listen to updates of already-in-place containers
                            disposable.add(cont.subscribe(entry -> consumer.accept(player, cont, entry), Util::handleError)));
                }, Util::handleError));
        return disposable;
    }

    public static <V> List<V> of(V v1) {
        List<V> list = new ArrayList<>();
        list.add(v1);
        return list;
    }

    public static <V> List<V> listOf(V v1, V v2) {
        List<V> list = of(v1);
        list.add(v2);
        return list;
    }

    public static <V> List<V> listOf(V v1, V v2, V v3) {
        List<V> list = listOf(v1, v2);
        list.add(v3);
        return list;
    }

    public static <V> List<V> listOf(V v1, V v2, V v3, V v4) {
        List<V> list = listOf(v1, v2, v3);
        list.add(v4);
        return list;
    }

    public static <V> List<V> listOf(V v1, V v2, V v3, V v4, V v5) {
        List<V> list = listOf(v1, v2, v3, v4);
        list.add(v5);
        return list;
    }

    public static <V> List<V> listOf(V v1, V v2, V v3, V v4, V v5, V v6) {
        List<V> list = listOf(v1, v2, v3, v4, v5);
        list.add(v6);
        return list;
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> map = of(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = of(k1, v1, k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3, k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
        map.put(k6, v6);
        return map;
    }
}
