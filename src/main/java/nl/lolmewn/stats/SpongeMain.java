package nl.lolmewn.stats;

import nl.lolmewn.stats.global.GlobalStats;
import nl.lolmewn.stats.listener.Playtime;
import nl.lolmewn.stats.listener.sponge.BlockBreak;
import nl.lolmewn.stats.listener.sponge.PlayerJoin;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.UUID;

@Plugin(id = "stats", name = "Stats", version = "5.0", description = "Stats collection", authors = "Lolmewn")
public class SpongeMain {

    private GlobalStats globalStats;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        StatManager.getInstance().addStat(new Stat("Blocks broken", "Amount of blocks broken"));
        StatManager.getInstance().addStat(new Stat("Playtime", "Amount of seconds played"));

        new PlayerJoin(this);
        new Playtime();
        new BlockBreak(this);

        this.globalStats = new GlobalStats();
        SharedMain.serverUuid = UUID.randomUUID().toString(); // todo
    }

    @Listener
    public void onServerEnd(GameStoppedEvent event) {
        this.globalStats.shutdown();
    }
}
