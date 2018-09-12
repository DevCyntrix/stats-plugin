package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;

public class PVPStat extends Stat {

    public PVPStat() {
        super("PVP Kills", "The amount of players you have slain");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.listOf(new StatMetaData("world", String.class, true),
                new StatMetaData("victim", String.class, true),
                new StatMetaData("weaponType", String.class, false),
                new StatMetaData("weaponName", String.class, false));
    }
}
