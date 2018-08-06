package nl.lolmewn.stats;

import org.bukkit.inventory.ItemStack;

public class BukkitUtil {

    public static String getWeaponName(ItemStack stack) {
        return stack == null
                ? "Fists"
                : (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                ? stack.getItemMeta().getDisplayName()
                : (stack.getType().name().substring(0, 1) + stack.getType().name().substring(1).toLowerCase().replace("_", " ")));
    }
}
