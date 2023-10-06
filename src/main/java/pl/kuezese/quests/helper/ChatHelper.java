package pl.kuezese.quests.helper;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class providing various helper methods for formatting and manipulating chat messages in the TikTokGame plugin.
 */
public final class ChatHelper {

    /**
     * Formats a single chat message by replacing color codes (using '&' as the color symbol) with appropriate Bukkit color codes.
     *
     * @param message The original chat message to be formatted.
     * @return The formatted chat message with color codes applied.
     */
    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Sends a title message to a player with specified title and subtitle text, fade-in, stay, and fade-out durations.
     *
     * @param p     The player to send the title to.
     * @param up    The title text to display.
     * @param down  The subtitle text to display.
     * @param fadeIn    The time in ticks for the title to fade in.
     * @param stay      The time in ticks for the title to stay on the screen.
     * @param fadeOut   The time in ticks for the title to fade out.
     */
    public static void title(Player p, String up, String down, int fadeIn, int stay, int fadeOut) {
        p.sendTitle(format(up), format(down), fadeIn, stay, fadeOut);
    }

    /**
     * Formats a list of chat messages by replacing color codes (using '&' as the color symbol) with appropriate Bukkit color codes.
     *
     * @param list The list of chat messages to be formatted.
     * @return The list of formatted chat messages with color codes applied.
     */
    public static List<String> format(List<String> list) {
        return list.stream().map(ChatHelper::format).collect(Collectors.toList());
    }

    /**
     * Replaces occurrences of a specific string with another string in a list of chat messages.
     *
     * @param list         The list of chat messages where replacements will be performed.
     * @param original     The original string to be replaced.
     * @param replacement  The string to replace occurrences of the original string.
     */
    public static void replace(List<String> list, String original, String replacement) {
        List<String> modifiedList = list.stream()
                .map(str -> str.replace(original, replacement))
                .toList();
        list.clear();
        list.addAll(modifiedList);
    }

    /**
     * Joins a portion of the array elements into a single string with the given separator.
     *
     * @param array     The array of strings to join.
     * @param separator The separator string to be used between elements.
     * @param start     The index of the first element to include in the joined string.
     * @param end       The index of the element after the last element to include in the joined string.
     * @return The joined string.
     */
    public static String join(String[] array, String separator, int start, int end) {
        int size = (end - start) * (((array[start] == null) ? 16 : array[start].length()) + separator.length());
        StringBuilder builder = new StringBuilder(size);
        for (int i = start; i < end; ++i) {
            if (i > start) {
                builder.append(separator);
            }
            if (array[i] != null) {
                builder.append(array[i]);
            }
        }
        return builder.toString();
    }
}