package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class BlockBreakStat extends Stat {

    public BlockBreakStat() {
        super("Blocks broken", "Amount of blocks broken");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.listOf(new StatMetaData("world", String.class, true),
                new StatMetaData("loc_x", Integer.class, false),
                new StatMetaData("loc_y", Integer.class, false),
                new StatMetaData("loc_z", Integer.class, false),
                new StatMetaData("material", String.class, true),
                new StatMetaData("tool", String.class, false));
    }
}
