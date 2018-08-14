package nl.lolmewn.stats;

import org.bukkit.NamespacedKey;

public class SimpleItem {

    private final NamespacedKey key;
    private final int amount;

    public SimpleItem(NamespacedKey key, int amount) {
        this.key = key;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
