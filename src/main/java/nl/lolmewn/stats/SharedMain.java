package nl.lolmewn.stats;

import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.stat.impl.*;

public class SharedMain {

    protected static String serverUuid;
    private static boolean isDebug = false;

    public static String getServerUuid() {
        return serverUuid;
    }

    public static void registerStats() {
        StatManager.getInstance().addStat(new PlaytimeStat());
        StatManager.getInstance().addStat(new BlockBreakStat());
        StatManager.getInstance().addStat(new BlockPlaceStat());
        StatManager.getInstance().addStat(new DeathStat());
        StatManager.getInstance().addStat(new KillStat());
    }

    public static void setDebug(boolean state) {
        isDebug = state;
    }

    public static void debug(String message) {
        if (isDebug) {
            System.out.println("[StatsDebug] " + message);
        }
    }
}
