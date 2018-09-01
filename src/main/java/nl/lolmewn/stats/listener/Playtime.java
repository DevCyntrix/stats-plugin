package nl.lolmewn.stats.listener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class Playtime {

    private Disposable disposable;

    public Playtime() {
        this.recordPlaytime();
    }

    private void recordPlaytime() {
        if (this.disposable != null) {
            // Already running
            return;
        }
        this.disposable = Observable.interval(1, TimeUnit.SECONDS).forEach(ignored ->
                Bukkit.getServer().getOnlinePlayers().forEach(player ->
                        PlayerManager.getInstance().getPlayer(player.getUniqueId()).subscribe(statsPlayer ->
                                StatManager.getInstance().getStat("Playtime").ifPresent(stat ->
                                        statsPlayer.getStats(stat).addEntry(
                                                new StatTimeEntry(System.currentTimeMillis(), 1,
                                                        Util.of("world", player.getWorld().getUID().toString()))
                                        )
                                ), Util::handleError
                        )
                )
        );
    }
}
