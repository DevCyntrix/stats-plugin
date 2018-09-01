package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class LastQuitStat extends Stat {

    public LastQuitStat() {
        super("Last quit", "When this player quit last");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.of(new StatMetaData("world", String.class, true));
    }

    @Override
    public String format(double value) {
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli((long) value), ZoneId.systemDefault());
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
