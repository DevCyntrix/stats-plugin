package nl.lolmewn.stats.listener;

import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoin implements Listener {

    public PlayerJoin(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerManager.getInstance().addPlayer(new StatsPlayer(event.getPlayer().getUniqueId()));
    }
}
