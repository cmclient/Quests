package pl.kuezese.quests.type;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum representing different types of quest actions.
 */
public @Getter enum QuestAction {

    /**
     * Represents a quest action related to an item.
     */
    ITEM("ITEM"),

    /**
     * Represents a quest action related to a mob (enemy or creature).
     */
    MOB("MOB");

    private final String name;

    /**
     * Constructor for QuestAction enum values.
     *
     * @param name The name of the quest action.
     */
    QuestAction(String name) {
        this.name = name;
    }

    /**
     * Get the QuestAction enum value by its name, ignoring case.
     *
     * @param name The name of the quest action to look up.
     * @return The QuestAction enum value if found, or null if not found.
     */
    public static QuestAction getByName(String name) {
        return Stream.of(QuestAction.values())
                .filter(action -> action.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a comma-separated string containing all available quest action types.
     *
     * @return A comma-separated string of quest action types.
     */
    public static String getTypes() {
        return Stream.of(QuestAction.values())
                .map(QuestAction::getName)
                .collect(Collectors.joining(", "));
    }
}
