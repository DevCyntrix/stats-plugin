package nl.lolmewn.stats.stat.impl;

import nl.lolmewn.stats.Util;
import nl.lolmewn.stats.player.StatMetaData;
import nl.lolmewn.stats.stat.Stat;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PlaytimeStat extends Stat {
    public PlaytimeStat() {
        super("Playtime", "");
    }

    @Override
    public Collection<StatMetaData> getMetaData() {
        return Util.of(new StatMetaData("world", String.class, true));
    }

    @Override
    public String format(double value) {
        long seconds = (long) value;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) -
                TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) -
                TimeUnit.DAYS.toMinutes(day) -
                TimeUnit.HOURS.toMinutes(hours);
        long second = TimeUnit.SECONDS.toSeconds(seconds) -
                TimeUnit.DAYS.toSeconds(day) -
                TimeUnit.HOURS.toSeconds(hours) -
                TimeUnit.MINUTES.toSeconds(minute);
        return day + " days, " + hours + " hours, " + minute + " minutes & " + second + " seconds";
    }

    @Override
    public String shortFormat(double value) {
        long seconds = (long) value;
        StringBuilder builder = new StringBuilder();
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        if (day > 0) builder.append(day).append('h');
        long hours = TimeUnit.SECONDS.toHours(seconds) -
                TimeUnit.DAYS.toHours(day);
        if (hours > 0) builder.append(hours).append('h');
        long minute = TimeUnit.SECONDS.toMinutes(seconds) -
                TimeUnit.DAYS.toMinutes(day) -
                TimeUnit.HOURS.toMinutes(hours);
        if (minute > 0) builder.append(minute).append('m');
        long second = TimeUnit.SECONDS.toSeconds(seconds) -
                TimeUnit.DAYS.toSeconds(day) -
                TimeUnit.HOURS.toSeconds(hours) -
                TimeUnit.MINUTES.toSeconds(minute);
        builder.append(second).append('s');
        return builder.toString();
    }
}
