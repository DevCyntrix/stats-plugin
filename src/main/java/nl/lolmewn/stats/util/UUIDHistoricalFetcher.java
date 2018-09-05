package nl.lolmewn.stats.util;

import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Interface to Mojang's API to fetch player UUIDs from player names.
 * <p>
 * Thanks to evilmidget38: http://forums.bukkit.org/threads/player-name-uuid-fetcher.250926/
 */
public class UUIDHistoricalFetcher implements Callable<Map<String, UUID>> {
    private static final long RATE_LIMIT = 600;
    private static final long RATE_LIMIT_INTERVAL = 10 * 60 * 1000;
    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private final JSONParser jsonParser = new JSONParser();
    private final Map<String, Long> names;

    public UUIDHistoricalFetcher(Map<String, Long> names) {
        this.names = ImmutableMap.copyOf(names);
    }

    private static HttpURLConnection createConnection(String username, long timestamp) throws IOException {
        URL url = new URL(PROFILE_URL + username + "?at=" + timestamp);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public Map<String, UUID> call() throws IOException, ParseException, InterruptedException {
        Map<String, UUID> uuidMap = new HashMap<>();
        int requests = (int) Math.ceil((double) names.size() / RATE_LIMIT);
        for (int i = 0; i < requests; i++) {
            List<Map.Entry<String, Long>> collect = this.names.entrySet().stream().skip(RATE_LIMIT * i).limit(RATE_LIMIT).collect(Collectors.toList());
            for (Map.Entry<String, Long> entry : collect) {
                HttpURLConnection connection = createConnection(entry.getKey(), entry.getValue());
                connection.connect();
                int code = connection.getResponseCode();
                if (code != 200) {
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entry.getValue() * 1000));
                    System.err.println("Response code " + code + " for name " + entry.getKey() + " at " + date + ", could not convert data :(");
                    continue;
                }
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JSONObject jsonProfile = (JSONObject) jsonParser.parse(reader);
                String id = (String) jsonProfile.get("id");
                String name = entry.getKey();
                UUID uuid = UUIDHistoricalFetcher.getUUID(id);
                uuidMap.put(name, uuid);
                System.out.println("Fetched UUID for " + name + ": " + uuid.toString());
            }
            if (i != requests - 1) {
                System.out.println("Waiting a while before getting new batch of UUIDs... Blame Mojang's Rate Limiting");
                Thread.sleep(RATE_LIMIT_INTERVAL);
            }
        }
        return uuidMap;
    }

}