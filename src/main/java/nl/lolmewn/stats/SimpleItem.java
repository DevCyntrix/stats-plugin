package nl.lolmewn.stats;

public class SimpleItem {

    private final String key;
    private final int amount;

    public SimpleItem(String key, int amount) {
        this.key = key;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getType() {
        return key;
    }
}
