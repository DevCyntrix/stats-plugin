package nl.lolmewn.stats.listener.bukkit;

import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.BukkitUtil;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

public class SimpleStatsListener implements Listener {

    public SimpleStatsListener(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Disposable addEntry(UUID uuid, String statName, StatTimeEntry entry) {
        return PlayerManager.getInstance().getPlayer(uuid).subscribe(statsPlayer ->
                StatManager.getInstance().getStat(statName).ifPresent(stat ->
                        statsPlayer.getStats(stat).addEntry(entry)
                )
        );
    }

    public Map<String, Object> getMetaData(LivingEntity entity) {
        return Map.of("world", entity.getWorld().getUID().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onArrowShot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        this.addEntry(event.getEntity().getUniqueId(), "Arrows shot",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getEntity())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onBedEnter(PlayerBedEnterEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Beds entered",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onBucketEmpty(PlayerBucketEmptyEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Buckets emptied",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getBucket().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onCommandPerformed(PlayerCommandPreprocessEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Commands performed",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        this.addEntry(event.getEntity().getUniqueId(), "Buckets emptied",
                new StatTimeEntry(System.currentTimeMillis(), Math.round(event.getFinalDamage()),
                        Map.of("world", event.getEntity().getWorld().getUID().toString(),
                                "cause", event.getCause().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onEggThrow(PlayerEggThrowEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Eggs thrown",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onFishCaught(PlayerFishEvent event) {
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        this.addEntry(event.getPlayer().getUniqueId(), "Fish caught",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getCaught().getType().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onFoodConsumed(PlayerItemConsumeEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Food consumed",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getItem().getType().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        this.addEntry(event.getWhoClicked().getUniqueId(), "Items crafted",
                new StatTimeEntry(System.currentTimeMillis(), event.getRecipe().getResult().getAmount(),
                        Map.of("world", event.getWhoClicked().getWorld().getUID().toString(),
                                "type", event.getRecipe().getResult().getType().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemDrop(PlayerDropItemEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Items dropped",
                new StatTimeEntry(System.currentTimeMillis(), event.getItemDrop().getItemStack().getAmount(),
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getItemDrop().getItemStack().getType().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        this.addEntry(event.getEntity().getUniqueId(), "Items picked up",
                new StatTimeEntry(System.currentTimeMillis(), event.getItem().getItemStack().getAmount(),
                        Map.of("world", event.getEntity().getWorld().getUID().toString(),
                                "type", event.getItem().getItemStack().getType().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerKick(PlayerKickEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Times kicked",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Last join",
                new StatTimeEntry(System.currentTimeMillis(), 0, getMetaData(event.getPlayer())));
        this.addEntry(event.getPlayer().getUniqueId(), "Times joined",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Last quit",
                new StatTimeEntry(System.currentTimeMillis(), 0, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) {
            return;
        }
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }
        this.addEntry(event.getPlayer().getUniqueId(), "Move",
                new StatTimeEntry(System.currentTimeMillis(), event.getFrom().distance(event.getTo()),
                        Map.of("world", event.getFrom().getWorld().getUID().toString(),
                                "type", getMoveType(event.getPlayer()))));
    }

    private String getMoveType(Player player) {
        if (player.isInsideVehicle()) {
            return player.getVehicle().getType().toString();
        }
        if (player.isSwimming()) {
            return "Swimming";
        }
        if (player.isGliding()) {
            return "Gliding";
        }
        if (player.isFlying()) {
            return "Flying";
        }
        if (player.isSprinting()) {
            return "Sprinting";
        }
        if (player.isSneaking()) {
            return "Sneaking";
        }
        return "Walking";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onEntityShear(PlayerShearEntityEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Times sheared",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getEntity().getType().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onTeleport(PlayerTeleportEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Teleports",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToolBreak(PlayerItemBreakEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Tools broken",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getBrokenItem().getType().getKey())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrade(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }
        MerchantInventory inventory = (MerchantInventory) event.getInventory();
        if (!event.getSlotType().equals(InventoryType.SlotType.RESULT)) {
            return;
        }
        if (!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && !event.getAction().equals(InventoryAction.PICKUP_ALL)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        this.addEntry(event.getWhoClicked().getUniqueId(), "Trades performed",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Map.of("world", event.getWhoClicked().getWorld().getUID().toString(),
                                "item", BukkitUtil.getSimpleItem(inventory.getSelectedRecipe().getResult()),
                                "price", BukkitUtil.getSimpleItems(inventory.getSelectedRecipe().getIngredients()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        int words = event.getMessage().split(" ").length;
        words += words == 0 ? 1 : 0;
        this.addEntry(event.getPlayer().getUniqueId(), "Words said",
                new StatTimeEntry(System.currentTimeMillis(), words, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerExpChangeEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "XP gained",
                new StatTimeEntry(System.currentTimeMillis(), event.getAmount(), getMetaData(event.getPlayer())));
    }
}
