package nl.lolmewn.stats.listener.bukkit;

import nl.lolmewn.stats.BukkitMain;
import nl.lolmewn.stats.storage.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.UUID;

public class WorldListener implements Listener {

    private WorldManager worldManager;

    public WorldListener(BukkitMain bukkitMain, WorldManager worldManager) {
        this.worldManager = worldManager;
        bukkitMain.getServer().getPluginManager().registerEvents(this, bukkitMain);
        this.saveCurrentWorlds();
    }

    private void saveCurrentWorlds() {
        Bukkit.getWorlds().forEach(world -> this.loadWorld(new WorldLoadEvent(world)));
    }

    @EventHandler()
    public void loadWorld(WorldLoadEvent event) {
        this.worldManager.addWorld(event.getWorld().getUID(), event.getWorld().getName());
        this.setWeather(event.getWorld().getUID(), event.getWorld().isThundering());
    }

    @EventHandler()
    public void onRain(WeatherChangeEvent event) {
        this.setWeather(event.getWorld().getUID(), event.toWeatherState());
    }

    private void setWeather(UUID uid, boolean thundering) {
        this.worldManager.setWeather(uid, thundering);
    }
}
