package nl.lolmewn.stats.listener.sponge;

import nl.lolmewn.stats.SpongeMain;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.HashMap;
import java.util.Map;

public class BlockBreak {

    public BlockBreak(SpongeMain plugin) {
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        event.getCause().first(Player.class).ifPresent(player -> {
            PlayerManager.getInstance().getPlayer(player.getUniqueId()).subscribe(statsPlayer -> {
                StatManager.getInstance().getStat("Blocks broken").ifPresent(stat -> {
                    Map<String, Object> metadata = generateMetadata(event);
                    statsPlayer.getStats(stat).addEntry(
                            new StatTimeEntry(System.currentTimeMillis(), 1, metadata)
                    );
                });
            });
        });
    }

    private Map<String, Object> generateMetadata(ChangeBlockEvent.Break event) {
        Map<String, Object> map = new HashMap<>();
        map.put("material", event.getTransactions().get(0).getOriginal().getState().getType().getName());
        return map;
    }
}
