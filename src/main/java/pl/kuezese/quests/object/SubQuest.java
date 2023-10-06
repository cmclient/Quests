package pl.kuezese.quests.object;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.Indyuce.mmoitems.api.Type;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.type.QuestAction;

import java.util.List;

/**
 * Represents a sub-quest in a larger quest. Sub-quests are used to define specific tasks or objectives within a quest.
 */
public @Getter @Builder class SubQuest {

    private final int id; // The id of this sub-quest.
    private transient @Setter Quest quest;
    private transient final QuestAction action; // The type of quest action associated with this sub-quest.
    private transient final int mmoCoreRequiredLvl;
    private transient final Type mmoItemType;
    private transient final String mmoItemId;
    private transient final String mobName;
    private transient final int amount; // The required amount or quantity to complete the sub-quest.
    private transient final List<String> rewards; // List of rewards given upon sub-quest completion.
    private transient final String title; // The title or name of the sub-quest.
    private transient final List<String> firstInteraction; // List of progress messages on player's first interaction with NPC.
    private transient final List<String> noRequiredLvl;
    private transient final List<String> progress; // List of progress messages or steps for the sub-quest.
    private transient final List<String> description; // Description of the sub-quest objectives.
    private transient final List<String> cantRedeem; // Messages indicating why the sub-quest cannot be redeemed.
    private transient final List<String> redeem; // Messages indicating how to redeem the sub-quest rewards.
    private transient final List<String> alreadyRedeemed; // Messages indicating that the sub-quest has already been redeemed.

    /**
     * Gets a SubQuest instance by its unique identifier.
     *
     * @param subQuestId The unique identifier of the subquest to retrieve.
     * @return The SubQuest instance with the given identifier.
     */
    public static SubQuest getById(int subQuestId) {
        return Quests.getInstance().getQuestManager().getQuests().values().stream()
                .flatMap(quest -> quest.getSubQuests().stream())
                .filter(subQuest -> subQuest.getId() == subQuestId).findAny()
                .orElse(null);
    }
}
