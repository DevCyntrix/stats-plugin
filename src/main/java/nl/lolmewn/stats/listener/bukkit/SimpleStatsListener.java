package nl.lolmewn.stats.listener.bukkit;

import io.reactivex.disposables.Disposable;
import nl.lolmewn.stats.BukkitMain;
import nl.lolmewn.stats.BukkitUtil;
import nl.lolmewn.stats.SharedMain;
import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.PlayerManager;
import nl.lolmewn.stats.player.StatTimeEntry;
import nl.lolmewn.stats.stat.StatManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class SimpleStatsListener implements Listener {

    private final BukkitMain plugin;

    public SimpleStatsListener(BukkitMain plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Disposable addEntry(UUID uuid, String statName, StatTimeEntry entry) {
        return PlayerManager.getInstance().getPlayer(uuid).subscribe(statsPlayer ->
                StatManager.getInstance().getStat(statName).ifPresent(stat ->
                        statsPlayer.getStats(stat).addEntry(entry)
                )
        );
    }

    public Map<String, Object> getMetaData(LivingEntity entity) {
        return Util.of("world", entity.getWorld().getUID().toString());
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
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", BukkitUtil.getMaterialType(event.getBucket()))));
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
        this.addEntry(event.getEntity().getUniqueId(), "Damage taken",
                new StatTimeEntry(System.currentTimeMillis(), Math.round(event.getFinalDamage()),
                        Util.of("world", event.getEntity().getWorld().getUID().toString(),
                                "type", event.getCause().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onEggThrow(PlayerEggThrowEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Eggs thrown",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getEgg().getType().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onFishCaught(PlayerFishEvent event) {
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        Item fish = (Item) event.getCaught();
        this.addEntry(event.getPlayer().getUniqueId(), "Fish caught",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", BukkitUtil.getItemType(fish.getItemStack()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onFoodConsumed(PlayerItemConsumeEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Food consumed",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", BukkitUtil.getItemType(event.getItem()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getResult() == Event.Result.DENY) {
            return;
        }
        int amountCreated;
        if (event.isShiftClick()) {
            amountCreated = Arrays.stream(event.getInventory().getMatrix())
                    .filter(stack -> stack != null && !Material.AIR.equals(stack.getType()))
                    .mapToInt(ItemStack::getAmount)
                    .reduce(Integer::min)
                    .orElse(0) * event.getRecipe().getResult().getAmount();
            // Check if it fits
            int stackSize = event.getRecipe().getResult().getMaxStackSize();
            int space = Arrays.stream(event.getWhoClicked().getInventory().getStorageContents())
                    .filter(stack -> stack == null || stack.isSimilar(event.getRecipe().getResult()))
            .mapToInt(stack -> stack == null ? stackSize : stackSize - stack.getAmount()).reduce(Integer::max).orElse(0);
            amountCreated = Math.min(amountCreated, space);
        } else {
            amountCreated = event.getRecipe().getResult().getAmount();
        }
        if (amountCreated == 0) return; // This shouldn't ever happen, but just in case
        this.addEntry(event.getWhoClicked().getUniqueId(), "Items crafted",
                new StatTimeEntry(System.currentTimeMillis(),  amountCreated,
                        Util.of("world", event.getWhoClicked().getWorld().getUID().toString(),
                                "type", BukkitUtil.getMaterialType(event.getRecipe().getResult().getType()))));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemDrop(PlayerDropItemEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Items dropped",
                new StatTimeEntry(System.currentTimeMillis(), event.getItemDrop().getItemStack().getAmount(),
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", BukkitUtil.getItemType(event.getItemDrop().getItemStack()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        this.addEntry(event.getEntity().getUniqueId(), "Items picked up",
                new StatTimeEntry(System.currentTimeMillis(), event.getItem().getItemStack().getAmount(),
                        Util.of("world", event.getEntity().getWorld().getUID().toString(),
                                "type", BukkitUtil.getItemType(event.getItem().getItemStack()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerKick(PlayerKickEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Times kicked",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()).subscribe(player ->
            StatManager.getInstance().getStat("Last join").ifPresent(stat -> {
                player.getStats(stat).resetWhere("world", event.getPlayer().getWorld().getUID().toString());
                this.addEntry(event.getPlayer().getUniqueId(), "Last join",
                        new StatTimeEntry(System.currentTimeMillis(), System.currentTimeMillis(), getMetaData(event.getPlayer())));
            })
        );
        this.addEntry(event.getPlayer().getUniqueId(), "Times joined",
                new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
        updateDatabase(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()).subscribe(player ->
            StatManager.getInstance().getStat("Last quit").ifPresent(stat -> {
                player.getStats(stat).resetWhere("world", event.getPlayer().getWorld().getUID().toString());
                this.addEntry(event.getPlayer().getUniqueId(), "Last quit",
                        new StatTimeEntry(System.currentTimeMillis(), System.currentTimeMillis(), getMetaData(event.getPlayer())));
            })
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onEntityShear(PlayerShearEntityEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Times sheared",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", event.getEntity().getType().toString())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onTeleport(PlayerTeleportEvent event) {
        TeleportCause teleportCause = event.getCause();
        if (teleportCause != TeleportCause.UNKNOWN && teleportCause != TeleportCause.SPECTATE) {
            this.addEntry(event.getPlayer().getUniqueId(), "Teleports",
                    new StatTimeEntry(System.currentTimeMillis(), 1, getMetaData(event.getPlayer())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToolBreak(PlayerItemBreakEvent event) {
        this.addEntry(event.getPlayer().getUniqueId(), "Tools broken",
                new StatTimeEntry(System.currentTimeMillis(), 1,
                        Util.of("world", event.getPlayer().getWorld().getUID().toString(),
                                "type", BukkitUtil.getItemType(event.getBrokenItem()))));
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
                        Util.of("world", event.getWhoClicked().getWorld().getUID().toString(),
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

    private void updateDatabase(Player player) {
        plugin.getMySQLStorage().onPlayerJoin(player.getUniqueId(), player.getName());
    }
}
