package pl.kuezese.quests.manager;

import com.google.common.base.Strings;
import lombok.Getter;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.helper.ChatHelper;
import pl.kuezese.quests.helper.ConfigHelper;
import pl.kuezese.quests.helper.CooldownHelper;
import pl.kuezese.quests.helper.NumberHelper;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.type.QuestAction;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Manages quests and sub-quests, loading them from a configuration file.
 */
public @Getter class QuestManager {

    private final Map<Integer, Quest> quests; // Collection to store loaded quests.

    /**
     * Constructs a new QuestManager.
     */
    public QuestManager() {
        this.quests = new ConcurrentHashMap<>();
    }

    /**
     * Find a quest by its ID.
     *
     * @param id The ID of the quest to find.
     * @return The quest if found, or null if not found.
     */
    public Quest findQuestById(int id) {
        return this.quests.get(id);
    }

    /**
     * Load quests and sub-quests from a configuration file.
     *
     * @param quests The Quests plugin instance.
     */
    public void load(Quests quests) {
        File file = new File(quests.getDataFolder(), "quests.yml");

        if (!file.exists()) {
            quests.saveResource("quests.yml", false);
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        cfg.getConfigurationSection("quests").getKeys(false).forEach(questId -> {
            String baseKey = "quests." + questId;
            Integer id = NumberHelper.parseNumber(questId);

            if (id == null || id < 0) {
                quests.getLogger().warning(String.format("Failed to load quest: %s. ID is not valid (not a number or below 0).", questId));
                return;
            }

            LinkedList<SubQuest> subQuests = new LinkedList<>();

            cfg.getConfigurationSection(ConfigHelper.getPath(baseKey, "subquests")).getKeys(false).forEach(subQuestId -> {
                String subQuestBaseKey = "quests." + questId + ".subquests." + subQuestId;
                QuestAction action = QuestAction.getByName(cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "action")));

                if (action == null) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. Action is not valid. Valid types are: " + QuestAction.getTypes(), questId));
                    return;
                }

                int mmoCoreRequiredLvl = cfg.getInt(ConfigHelper.getPath(subQuestBaseKey, "mmocore-required-lvl"));

                if (mmoCoreRequiredLvl < 0) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. mmocore-required-lvl cannot be below 0.", subQuests));
                    return;
                }

                boolean isItem = action == QuestAction.ITEM;
                Type mmoItemType = isItem ? Type.get(cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "mmoitem-type"))) : null;

                if (isItem && mmoItemType == null) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. mmoitem-type cannot be empty.", questId));
                    return;
                }

                String mmoItemId = isItem ? cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "mmoitem-id")) : null;

                if (isItem && Strings.isNullOrEmpty(mmoItemId)) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. mmoitem-id cannot be empty.", questId));
                    return;
                }

                String mobName = isItem ? null : ChatHelper.format(cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "mob-name")));

                if (!isItem && Strings.isNullOrEmpty(mobName)) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. Mob name cannot be empty.", questId));
                    return;
                }

                int amount = cfg.getInt(ConfigHelper.getPath(subQuestBaseKey, "amount"));

                if (amount < 0) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. Amount cannot be below 0.", subQuests));
                    return;
                }

                Duration cooldown = isItem ? CooldownHelper.parseDuration(cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "cooldown"))) : null;

                if (isItem && cooldown == null) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. Cooldown is invalid.", subQuests));
                    return;
                }

                double chance = isItem ? cfg.getDouble(ConfigHelper.getPath(subQuestBaseKey, "chance")) : 0.0D;

                if (isItem && chance == 0.0D) {
                    quests.getLogger().warning(String.format("Failed to load subquest: %s. Chance is invalid.", subQuests));
                    return;
                }

                List<String> rewards = Collections.unmodifiableList(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "rewards")));

                if (rewards.isEmpty()) {
                    quests.getLogger().warning(String.format("Rewards of subquest %s are empty.", questId));
                }

                String title = ChatHelper.format(cfg.getString(ConfigHelper.getPath(subQuestBaseKey, "title")));
                List<String> firstInteraction = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "first-interaction"))));
                List<String> noRequiredLvl = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "no-required-lvl"))));
                List<String> progress = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "progress"))));
                List<String> description = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "description"))));
                List<String> cantRedeem = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "cant-redeem"))));
                List<String> redeem = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "redeem"))));
                List<String> alreadyRedeemed = Collections.unmodifiableList(ChatHelper.format(cfg.getStringList(ConfigHelper.getPath(subQuestBaseKey, "already-redeemed"))));

                subQuests.add(SubQuest.builder()
                        .id(subQuestId.hashCode())
                        .action(action)
                        .mmoCoreRequiredLvl(mmoCoreRequiredLvl)
                        .mmoItemType(mmoItemType)
                        .mmoItemId(mmoItemId)
                        .mobName(mobName)
                        .amount(amount)
                        .cooldown(cooldown)
                        .chance(chance)
                        .rewards(rewards)
                        .title(title)
                        .firstInteraction(firstInteraction)
                        .noRequiredLvl(noRequiredLvl)
                        .progress(progress)
                        .description(description)
                        .cantRedeem(cantRedeem)
                        .redeem(redeem)
                        .alreadyRedeemed(alreadyRedeemed)
                        .build());
            });

            Quest quest = Quest.builder()
                    .id(id)
                    .subQuests(subQuests)
                    .build();

            quest.getSubQuests().forEach(subQuest -> subQuest.setQuest(quest));
            this.quests.put(quest.getId(), quest);
        });

        quests.getLogger().info(String.format("Loaded %d quests with %d sub-quests.", this.quests.size(), this.quests.values().stream().flatMapToInt(q -> IntStream.of(q.getSubQuests().size())).count()));
    }
}
