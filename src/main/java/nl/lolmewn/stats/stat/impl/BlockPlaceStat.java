package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.List;

public class BlockPlaceStat extends Stat {

    public BlockPlaceStat() {
        super("Blocks placed", "Amount of blocks placed");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return List.of(new StatMetaData("world", String.class, true),
                new StatMetaData("loc_x", Integer.class, false),
                new StatMetaData("loc_y", Integer.class, false),
                new StatMetaData("loc_z", Integer.class, false),
                new StatMetaData("material", String.class, true));
    }
}
