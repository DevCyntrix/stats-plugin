package nl.lolmewn.stats;

import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.stat.impl.BlockBreakStat;
import nl.lolmewn.stats.stat.impl.PlaytimeStat;

public class SharedMain {

    protected static String serverUuid;

    public static String getServerUuid() {
        return serverUuid;
    }

    public static void registerStats() {
        StatManager.getInstance().addStat(new PlaytimeStat());
        StatManager.getInstance().addStat(new BlockBreakStat());
    }
}
