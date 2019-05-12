package nl.lolmewn.stats.signs;

import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BukkitSignListener implements Listener {

    private Map<UUID, BukkitStatSignInstallProgress> installers = new HashMap<>();

    public BukkitSignListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void signPlace(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("[stats]")) {
            return;
        }
        if (!event.getPlayer().hasPermission("stats.sign.place")) {
            event.getPlayer().sendMessage("No permissions");
            cancelEvent(event);
            return;
        }
        BukkitStatSignInstallProgress progress = new BukkitStatSignInstallProgress();
        progress.pid = event.getPlayer().getUniqueId();
        progress.state = SignInstallState.STAT_MODE;
        progress.location = event.getBlock().getLocation();
        this.installers.put(event.getPlayer().getUniqueId(), progress);
        event.getPlayer().sendMessage("Setting up a new Stats sign!");
        event.getPlayer().sendMessage("Please tell me what kind of sign you want");
        event.getPlayer().sendMessage("You can simply type it into the chat ");
        event.getPlayer().sendMessage(Arrays.toString(StatsSignStatMode.values()).substring(1).replace("]", ""));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void chat(AsyncPlayerChatEvent event) {
        Optional<BukkitStatSignInstallProgress> installer = this.installers.values().stream().filter(inst -> inst.pid == event.getPlayer().getUniqueId()).findAny();
        if (installer.isPresent()) {
            event.setCancelled(true);
            switch (installer.get().state) {
                case PLAYER:
                    this.handlePlayer(event, installer.get());
                    break;
                case PLAYER_MODE:
                    this.handlePlayerMode(event, installer.get());
                    break;
                case STAT:
                    this.handleStat(event, installer.get());
                    break;
                case STAT_MODE:
                    this.handleStatMode(event, installer.get());
                    break;
            }
        }
    }

    private void handlePlayer(AsyncPlayerChatEvent event, BukkitStatSignInstallProgress installer) {
        if (event.getMessage().equalsIgnoreCase("done")) {
            event.getPlayer().sendMessage("Cool! We're done here. Stat sign created!");
            this.finish(installer);
            return;
        }
        OfflinePlayer player = Bukkit.getPlayer(event.getMessage());
        if (player == null) {
            player = Bukkit.getOfflinePlayer(event.getMessage());
        }
        if (!player.hasPlayedBefore()) {
            event.getPlayer().sendMessage("This player has never been seen on this server, assuming you made a typo.");
            event.getPlayer().sendMessage("Please try again");
            return;
        }
        installer.players.add(player.getUniqueId());
        if (installer.playerMode == StatsSignPlayerMode.SINGLE) {
            event.getPlayer().sendMessage("Cool! We're done here. Stat sign created!");
            this.finish(installer);
        } else {
            event.getPlayer().sendMessage("Player " + player.getName() + " added. Any other players?");
            event.getPlayer().sendMessage("Or type 'done' if you have added all players you wish.");
        }

    }

    private void handlePlayerMode(AsyncPlayerChatEvent event, BukkitStatSignInstallProgress installer) {
        Optional<StatsSignPlayerMode> playerMode = Arrays.stream(StatsSignPlayerMode.values()).filter(mode -> mode.name().equalsIgnoreCase(event.getMessage())).findAny();
        if (!playerMode.isPresent()) {
            event.getPlayer().sendMessage("Could not find player mode: " + event.getMessage());
            event.getPlayer().sendMessage("Please choose from the following options:");
            event.getPlayer().sendMessage(Arrays.toString(StatsSignPlayerMode.values()).substring(1).replace("]", ""));
        } else {
            installer.playerMode = playerMode.get();
            switch (playerMode.get()) {
                case MULTIPLE:
                    event.getPlayer().sendMessage("Please enter the names of the players you wish to show on this sign, one by one");
                    event.getPlayer().sendMessage("When you've added all the names, type 'done'");
                    installer.state = SignInstallState.PLAYER;
                    break;
                case SINGLE:
                    event.getPlayer().sendMessage("Please enter the name of the player you wish to show");
                    installer.state = SignInstallState.PLAYER;
                    break;
                case RANDOM:
                    event.getPlayer().sendMessage("Cool! We're done here. Stat sign created!");
                    this.finish(installer);
                    break;
                default:
                    event.getPlayer().sendMessage("Unknown type: " + playerMode.get());
            }
        }
    }

    private void finish(BukkitStatSignInstallProgress installer) {
        this.installers.remove(installer.pid);
        StatsSign sign = new BukkitStatsSign(UUID.randomUUID(), installer.location.getBlockX(), installer.location.getBlockY(),
                installer.location.getBlockZ(), installer.location.getWorld().getUID(),
                new StatsSignSpec(installer.playerMode, installer.statMode, installer.players, installer.stats));
        SignManager.getInstance().addSign(sign);
    }

    private void handleStat(AsyncPlayerChatEvent event, BukkitStatSignInstallProgress installer) {
        if (event.getMessage().equalsIgnoreCase("done")) {
            event.getPlayer().sendMessage("Please enter the player type for this sign");
            event.getPlayer().sendMessage("You can choose from the following options: ");
            event.getPlayer().sendMessage(Arrays.toString(StatsSignPlayerMode.values()).substring(1).replace("]", ""));
            installer.state = SignInstallState.PLAYER_MODE;
            return;
        }
        Optional<Stat> stat = StatManager.getInstance().getStat(event.getMessage());
        if (!stat.isPresent()) {
            event.getPlayer().sendMessage("Could not find stat: " + event.getMessage());
            event.getPlayer().sendMessage("Please try again");
        } else {
            installer.stats.add(stat.get());
            if (installer.statMode == StatsSignStatMode.SINGLE) {
                event.getPlayer().sendMessage("Please enter the player type for this sign");
                event.getPlayer().sendMessage("You can choose from the following options: ");
                event.getPlayer().sendMessage(Arrays.toString(StatsSignPlayerMode.values()).substring(1).replace("]", ""));
                installer.state = SignInstallState.PLAYER_MODE;
            } else {
                event.getPlayer().sendMessage("Stat " + stat.get().getName() + " added. Any other stats?");
                event.getPlayer().sendMessage("Or type 'done' if you have added all stats you wish.");
            }
        }
    }

    private void handleStatMode(AsyncPlayerChatEvent event, BukkitStatSignInstallProgress installer) {
        Optional<StatsSignStatMode> statMode = Arrays.stream(StatsSignStatMode.values()).filter(mode -> mode.name().equalsIgnoreCase(event.getMessage())).findAny();
        if (!statMode.isPresent()) {
            event.getPlayer().sendMessage("Could not find stat mode: " + event.getMessage());
            event.getPlayer().sendMessage("Please tell me what kind of sign you want");
            event.getPlayer().sendMessage("You can simply type it into the chat ");
            event.getPlayer().sendMessage(Arrays.toString(StatsSignStatMode.values()).substring(1).replace("]", ""));
        } else {
            installer.statMode = statMode.get();
            switch (statMode.get()) {
                case MULTIPLE:
                    event.getPlayer().sendMessage("Please enter the names of the players you wish to show on this sign, one by one");
                    event.getPlayer().sendMessage("When you've added all the names, type 'done'");
                    installer.state = SignInstallState.STAT;
                    break;
                case SINGLE:
                    event.getPlayer().sendMessage("Please enter the name of the stat you wish to show");
                    event.getPlayer().sendMessage("You can choose from the following:");
                    event.getPlayer().sendMessage(StatManager.getInstance().getStats().toString());
                    installer.state = SignInstallState.STAT;
                    break;
                case RANDOM:
                    event.getPlayer().sendMessage("Please enter the player type for this sign");
                    event.getPlayer().sendMessage("You can choose from the following options: ");
                    event.getPlayer().sendMessage(Arrays.toString(StatsSignPlayerMode.values()).substring(1).replace("]", ""));
                    installer.state = SignInstallState.PLAYER_MODE;
                    break;
                default:
                    event.getPlayer().sendMessage("Unknown type: " + statMode.get());
            }
        }
    }

    public void cancelEvent(SignChangeEvent event) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.SIGN, 1));
    }

    private enum SignInstallState {STAT, PLAYER, STAT_MODE, PLAYER_MODE}

    private class BukkitStatSignInstallProgress {
        private Location location;
        private UUID pid;
        private Collection<Stat> stats = new ArrayList<>();
        private Collection<UUID> players = new ArrayList<>();
        private StatsSignPlayerMode playerMode;
        private StatsSignStatMode statMode;
        private SignInstallState state;
    }
}
