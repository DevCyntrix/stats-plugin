package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class ArrowsShotStat extends Stat {

    public ArrowsShotStat() {
        super("Arrows shot", "Amount of arrows fired");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.of(new StatMetaData("world", String.class, true));
    }
}
