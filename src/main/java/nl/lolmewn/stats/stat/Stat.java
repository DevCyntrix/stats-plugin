package nl.lolmewn.stats.stat;

import nl.lolmewn.stats.player.StatMetaData;

import java.text.DecimalFormat;
import java.util.Collection;

public abstract class Stat {

    private final String name;
    private final String description;

    public Stat(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String format(double value) {
        return new DecimalFormat("#,##0.##").format(value);
//        return String.format("%1$,.2f", value);
    }

    public String shortFormat(double value) {
        return new DecimalFormat("#,##0").format(value);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract Collection<StatMetaData> getMetaData();
}
