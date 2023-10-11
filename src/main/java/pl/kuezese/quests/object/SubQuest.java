package pl.kuezese.quests.object;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.Indyuce.mmoitems.api.Type;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.type.QuestAction;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Represents a sub-quest in a larger quest. Sub-quests are used to define specific tasks or objectives within a quest.
 */
public @Getter @Builder class SubQuest {

    private final int id; // The id of this sub-quest.
    private transient @Setter Quest quest; // The parent quest associated with this sub-quest.
    private transient final QuestAction action; // The type of quest action associated with this sub-quest.
    private transient final int mmoCoreRequiredLvl; // The required level in the MMO Core for this sub-quest.
    private transient final Type mmoItemType; // The type of MMO item required for this sub-quest.
    private transient final String mmoItemId; // The unique identifier of the MMO item required for this sub-quest.
    private transient final String mobName; // The name of the mob associated with this sub-quest.
    private transient final int amount; // The required amount or quantity to complete the sub-quest.
    private transient final Duration cooldown; // The cooldown duration for this sub-quest.
    private transient final double chance; // The chance for this sub-quest.
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

    /**
     * Checks if a user is on cooldown for this sub-quest.
     *
     * @param user The user for whom to check the cooldown.
     * @return true if the user is on cooldown, false otherwise.
     */
    public boolean isCooldown(User user) {
        Instant currentTime = Instant.now();
        Instant cooldownExpirationTime = user.getCooldownSubQuests().get(this);

        return Optional.ofNullable(cooldownExpirationTime)
                .map(currentTime::isBefore)
                .orElse(false);
    }

    /**
     * Get the remaining cooldown in a concise format like "XdYhZmWs".
     *
     * @param user The user for whom to check the remaining cooldown.
     * @return The remaining cooldown formatted in a concise way.
     */
    public String getRemainingCooldown(User user) {
        Instant currentTime = Instant.now();
        Instant cooldownExpirationTime = user.getCooldownSubQuests().get(this);

        if (cooldownExpirationTime != null && currentTime.isBefore(cooldownExpirationTime)) {
            Duration remainingDuration = Duration.between(currentTime, cooldownExpirationTime);
            long days = remainingDuration.toDays();
            long hours = remainingDuration.minusDays(days).toHours();
            long minutes = remainingDuration.minusMinutes(hours).toMinutes();
            long seconds = remainingDuration.minusMinutes(minutes).toSeconds();
            long millis = remainingDuration.minusSeconds(seconds).toMillis();

            StringBuilder remainingCooldown = new StringBuilder();
            if (days > 0) {
                remainingCooldown.append(days).append("d");
            }
            if (hours > 0) {
                remainingCooldown.append(hours).append("h");
            }
            if (minutes > 0) {
                remainingCooldown.append(minutes).append("m");
            }
            if (seconds > 0) {
                remainingCooldown.append(seconds).append("s");
            }
            if (remainingCooldown.isEmpty() && millis > 0) {
                remainingCooldown.append(millis).append("ms");
            }

            return remainingCooldown.toString();
        }

        return "0s";
    }

    public double getChance(User user) {
        return this.getChance() * user.getChanceModifiers().getOrDefault(this.quest, 1.0);
    }
}
