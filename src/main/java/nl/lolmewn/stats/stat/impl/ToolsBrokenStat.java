package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.List;

public class ToolsBrokenStat extends Stat {
    public ToolsBrokenStat() {
        super("Tools broken", "Amount of tools broken");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return List.of(new StatMetaData("world", String.class, true),
                new StatMetaData("type", String.class, true));
    }
}
