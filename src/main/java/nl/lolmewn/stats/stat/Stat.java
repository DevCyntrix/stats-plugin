package nl.lolmewn.stats.stat;

public class Stat {

    private final String name;
    private final String description;

    public Stat(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
