package pl.kuezese.quests.object;

import lombok.Builder;
import lombok.Getter;
import pl.kuezese.quests.Quests;

import java.util.List;

/**
 * Represents a quest, which consists of a unique identifier and a list of sub-quests.
 */
public @Getter @Builder class Quest {

    private final int id; // The unique identifier for the quest.
    private transient final List<SubQuest> subQuests; // The list of sub-quests associated with this quest.

    /**
     * Gets a Quest instance by its unique identifier.
     *
     * @param questId The unique identifier of the quest to retrieve.
     * @return The Quest instance with the given identifier.
     */
    public static Quest getById(int questId) {
        return Quests.getInstance().getQuestManager().findQuestById(questId);
    }
}