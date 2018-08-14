package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.SimpleItem;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.List;

public class TradesPerformedStat extends Stat {

    public TradesPerformedStat() {
        super("Trades performed", "Amount of trades done with villagers");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return List.of(new StatMetaData("world", String.class, true),
                new StatMetaData("item", SimpleItem.class, false),
                new StatMetaData("price", List.class, false));
    }
}
