/**
 * A utility class for parsing duration strings and converting them into Java's Duration objects.
 */
package pl.kuezese.quests.helper;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class CooldownHelper {

    /**
     * Parses a duration string and converts it into a Duration object.
     *
     * @param durationString A string representing the duration. The format should consist of numerical values
     *                      followed by units (e.g., "5s" for 5 seconds, "2h" for 2 hours).
     * @return A Duration object representing the parsed duration, or null if the input string is null.
     */
    public static @Nullable Duration parseDuration(String durationString) {
        if (durationString == null) {
            return null;
        }

        long milliseconds = 0;

        // Split the duration string into numerical values and units.
        String[] parts = durationString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        // Iterate through the parts and accumulate milliseconds based on units.
        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            String unit = parts[i + 1].toLowerCase();

            switch (unit) {
                case "s" -> milliseconds += value * 1000L;
                case "m" -> milliseconds += (long) value * 60 * 1000;
                case "h" -> milliseconds += (long) value * 60 * 60 * 1000;
                case "d" -> milliseconds += (long) value * 24 * 60 * 60 * 1000;
            }
        }

        // Create and return a Duration object representing the parsed duration.
        return Duration.ofMillis(milliseconds);
    }
}
