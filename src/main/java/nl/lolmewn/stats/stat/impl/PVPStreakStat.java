package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class PVPStreakStat extends Stat {

    public PVPStreakStat() {
        super("PVP kill streak", "Amount of kills gotten in a row without dying");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.of(new StatMetaData("world", String.class, true));
    }
}
