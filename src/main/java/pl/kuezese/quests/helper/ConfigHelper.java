package pl.kuezese.quests.helper;

/**
 * A utility class for constructing configuration paths.
 */
public final class ConfigHelper {

    /**
     * Constructs a configuration path by joining a base key and an additional key using dots.
     *
     * @param baseKey     The base key.
     * @param additional  The additional key to append.
     * @return The constructed configuration path.
     */
    public static String getPath(String baseKey, String additional) {
        return String.join(".", baseKey, additional);
    }
}
