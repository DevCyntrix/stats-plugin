package nl.lolmewn.stats;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final Pattern PATTERN_UUID = Pattern.compile("^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_HEXED_UUID = Pattern.compile("^([a-z0-9]{8})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{12})$", Pattern.CASE_INSENSITIVE);

    public static Optional<UUID> generateUUID(String hex) {
        try {
            if (!PATTERN_UUID.matcher(hex).matches()) {
                Matcher matcher = PATTERN_HEXED_UUID.matcher(hex);
                if (matcher.matches()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        if (i != 1) {
                            sb.append("-");
                        }
                        sb.append(matcher.group(i));
                    }
                    return Optional.of(UUID.fromString(sb.toString()));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }
}
