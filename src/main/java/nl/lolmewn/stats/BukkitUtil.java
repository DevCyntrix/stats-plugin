package nl.lolmewn.stats;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitUtil {

    public static String getWeaponName(ItemStack stack) {
        return stack == null
                ? "Fists"
                : (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                ? stack.getItemMeta().getDisplayName()
                : (stack.getType().name().substring(0, 1) + stack.getType().name().substring(1).toLowerCase().replace("_", " ")));
    }

    public static String getItemType(ItemStack stack) {
        return getMaterialType(stack.getType());
    }

    public static String getMaterialType(Material material) {
        return "minecraft:" + material.name().toLowerCase();
    }

    public static SimpleItem getSimpleItem(ItemStack stack) {
        return new SimpleItem(getItemType(stack), stack.getAmount());
    }

    public static List<SimpleItem> getSimpleItems(List<ItemStack> ingredients) {
        return ingredients.stream().map(BukkitUtil::getSimpleItem).collect(Collectors.toList());
    }

    public static int getRoomFor(ItemStack[] contents, ItemStack toFit) {
        int maxStackSize = toFit.getMaxStackSize();
        return Arrays.stream(contents)
                .filter(stack -> stack == null || stack.isSimilar(toFit))
                .mapToInt(stack -> stack == null ? maxStackSize : maxStackSize - stack.getAmount())
                .sum();
    }
}
