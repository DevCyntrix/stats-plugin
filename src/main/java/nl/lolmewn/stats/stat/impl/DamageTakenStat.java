package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class DamageTakenStat extends Stat {

    public DamageTakenStat() {
        super("Damage taken", "Amount of damage taken");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.listOf(new StatMetaData("world", String.class, true),
                new StatMetaData("type", String.class, true));
    }

    @Override
    public String format(double value) {
        return String.format("%1$,.1f hearts", value / 2);
    }
}
