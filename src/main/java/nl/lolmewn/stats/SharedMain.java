package nl.lolmewn.stats;

import nl.lolmewn.stats.stat.StatManager;
import nl.lolmewn.stats.stat.impl.*;

import java.util.logging.Logger;

public class SharedMain {

    protected static String serverUuid;
    private static boolean isDebug = false;

    protected static Logger log;

    public static String getServerUuid() {
        return serverUuid;
    }

    public static void registerStats() {
        StatManager.getInstance().addStat(new ArrowsShotStat());
        StatManager.getInstance().addStat(new BedsEnteredStat());
        StatManager.getInstance().addStat(new BucketsEmptiedStat());
        StatManager.getInstance().addStat(new CommandsPerformedStat());
        StatManager.getInstance().addStat(new DamageTakenStat());
        StatManager.getInstance().addStat(new EggsThrownStat());
        StatManager.getInstance().addStat(new FishCaughtStat());
        StatManager.getInstance().addStat(new FoodConsumedStat());
        StatManager.getInstance().addStat(new ItemsCraftedStat());
        StatManager.getInstance().addStat(new ItemsDroppedStat());
        StatManager.getInstance().addStat(new ItemsPickedUpStat());
        StatManager.getInstance().addStat(new KickedStat());
        StatManager.getInstance().addStat(new LastJoinStat());
        StatManager.getInstance().addStat(new LastQuitStat());
        StatManager.getInstance().addStat(new MoveStat());
        StatManager.getInstance().addStat(new PVPStat());
        StatManager.getInstance().addStat(new PVPStreakStat());
        StatManager.getInstance().addStat(new ShearStat());
        StatManager.getInstance().addStat(new TeleportStat());
        StatManager.getInstance().addStat(new TimesJoinedStat());
        StatManager.getInstance().addStat(new ToolsBrokenStat());
        StatManager.getInstance().addStat(new TradesPerformedStat());
        StatManager.getInstance().addStat(new WordsSaidStat());
        StatManager.getInstance().addStat(new XpGainedStat());

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
            log.info("[StatsDebug] " + message);
        }
    }
}
