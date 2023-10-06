package pl.kuezese.quests.helper;

import org.jetbrains.annotations.Nullable;

/**
 * A utility class for parsing numbers from strings.
 */
public final class NumberHelper {

    /**
     * Parses an integer from a string.
     *
     * @param s The string to parse.
     * @return The parsed integer, or null if parsing fails.
     */
    public static @Nullable Integer parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
