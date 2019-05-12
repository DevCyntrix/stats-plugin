package nl.lolmewn.stats.signs;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatsContainer;
import nl.lolmewn.stats.player.StatsPlayer;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;

import java.util.*;

public abstract class StatsSign implements Runnable {

    private final UUID id;
    private final int x, y, z;
    private final UUID world;
    private final StatsSignSpec spec;
    private double value;

    private Collection<Disposable> subscriptions = new ArrayList<>();

    public StatsSign(UUID uuid, int x, int y, int z, UUID world, StatsSignSpec spec) {
        this.id = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.spec = spec;

        this.start();
    }

    private void start() {
        this.subscriptions.forEach(Disposable::dispose);
        this.subscriptions.clear();
        Stat stat = this.pickStat();
        if (stat == null) return;
        this.value = 0;
        if (spec.getPlayerMode() == StatsSignPlayerMode.SINGLE) {
            Observable<StatsPlayer> playerObservable = PlayerManager.getInstance().getPlayer(spec.getPlayers().iterator().next());
            this.subscriptions.add(playerObservable.subscribe(player -> startPlayer(player, stat), Util::handleError));
        } else if (spec.getPlayerMode() == StatsSignPlayerMode.RANDOM) {
            UUID playerUUID = this.getOnlinePlayers().get(new Random().nextInt(this.getOnlinePlayers().size()));
            Observable<StatsPlayer> playerObservable = PlayerManager.getInstance().getPlayer(playerUUID);
            this.subscriptions.add(playerObservable.subscribe(player -> startPlayer(player, stat), Util::handleError));
        } else {
            this.subscriptions.add(PlayerManager.getInstance().subscribe(player -> startPlayer(player, stat), Util::handleError));
        }
    }

    private Stat pickStat() {
        if (this.spec.getStats().size() == 0) return null;
        switch (this.spec.getStatMode()) {
            case SINGLE:
                return this.spec.getStats().iterator().next();
            case RANDOM:
                return new ArrayList<>(StatManager.getInstance().getStats()).get(new Random().nextInt(StatManager.getInstance().getStats().size()));
            case MULTIPLE:
            default:
                return new ArrayList<>(this.spec.getStats()).get(new Random().nextInt(this.spec.getStats().size()));
        }
    }

    private void startPlayer(StatsPlayer player, Stat stat) {
        StatsContainer container = player.getStats(stat);
        this.value += container.getTotal();
        this.update(player, stat);
        this.subscriptions.add(container.subscribe(entry -> {
            this.value += entry.getAmount();
            this.update(player, stat);
        }, Util::handleError));
    }

    private void update(StatsPlayer player, Stat stat) {
        String line1 = "[Stats]";
        String line2 = stat != null ? stat.getName() : "Loading...";
        String line3 = player != null ? this.getPlayerName(player.getUuid()) : "Loading...";
        String line4 = stat != null ? stat.shortFormat(this.value) : "Loading...";
        this.updateSign(line1, line2, line3, line4);
    }

    @Override
    public void run() {

    }

    public UUID getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorld() {
        return world;
    }

    public StatsSignSpec getSpec() {
        return spec;
    }

    protected abstract List<UUID> getOnlinePlayers();

    protected abstract void updateSign(String line1, String line2, String line3, String line4);

    protected abstract String getPlayerName(UUID uuid);
}
