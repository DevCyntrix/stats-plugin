package nl.lolmewn.stats.player;

import java.util.UUID;

public class MySQLStatsPlayer extends StatsPlayer {

    private int dbId;

    public MySQLStatsPlayer(UUID uuid, int dbId) {
        super(uuid);
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
