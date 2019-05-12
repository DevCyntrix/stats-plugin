package nl.lolmewn.stats.signs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignManager {

    private static SignManager signManager;
    private Map<UUID, StatsSign> signs = new HashMap<>();

    public static SignManager getInstance() {
        if (signManager == null) signManager = new SignManager();
        return signManager;
    }

    public void addSign(StatsSign sign) {
        this.signs.put(sign.getId(), sign);
    }

    public StatsSign getSign(UUID uuid) {
        return signs.get(uuid);
    }
}
