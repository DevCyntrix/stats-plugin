package nl.lolmewn.stats.listener.sponge;

import nl.lolmewn.stats.SpongeMain;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatsPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerJoin {

    public PlayerJoin(SpongeMain plugin) {
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        PlayerManager.getInstance().addPlayer(new StatsPlayer(event.getTargetEntity().getUniqueId()));
    }
}
