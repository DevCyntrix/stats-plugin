package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class CommandsPerformedStat extends Stat {

    public CommandsPerformedStat() {
        super("Commands performed", "Amount of commands performed");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.of(new StatMetaData("world", String.class, true));
    }
}
