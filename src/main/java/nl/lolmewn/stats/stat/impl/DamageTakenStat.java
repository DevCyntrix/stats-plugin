package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.List;

public class DamageTakenStat extends Stat {

    public DamageTakenStat() {
        super("Damage taken", "Amount of damage taken");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return List.of(new StatMetaData("world", String.class, true),
                new StatMetaData("cause", String.class, true));
    }

    @Override
    public String format(double value) {
        return String.format("%1$,.1f hearts", value / 2);
    }
}
