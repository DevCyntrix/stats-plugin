package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.List;

public class DeathStat extends Stat {
    public DeathStat() {
        super("Deaths", "Number of player deaths");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return List.of(
                new StatMetaData("cause", String.class, true),
                new StatMetaData("world", String.class, true),
                new StatMetaData("loc_x", Double.class, false),
                new StatMetaData("loc_y", Double.class, false),
                new StatMetaData("loc_z", Double.class, false)
        );
    }
}
