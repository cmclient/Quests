package pl.kuezese.quests.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class UserSerializer {

    /**
     * Serializes the progress of a user into a JSON array.
     *
     * @param user The user whose progress should be serialized.
     * @return A JSON array containing the serialized progress.
     */
    public static JsonArray serializeProgress(User user) {
        return user.getProgress().entrySet().stream()
                .map(entry -> {
                    JsonObject questObject = new JsonObject();
                    questObject.addProperty("subquest-id", entry.getKey().getId());
                    questObject.addProperty("progress", entry.getValue().get());
                    return questObject;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    /**
     * Serializes the active sub-quests of a user into a JSON array.
     *
     * @param user The user whose active sub-quests should be serialized.
     * @return A JSON array containing the serialized active sub-quests.
     */
    public static JsonArray serializeActiveSubQuests(User user) {
        return user.getActiveSubQuests().entrySet().stream()
                .map(entry -> {
                    JsonObject questObject = new JsonObject();
                    questObject.addProperty("quest-id", entry.getKey().getId());

                    JsonObject subQuestObject = new JsonObject();
                    subQuestObject.addProperty("subquest-id", entry.getValue().getId());

                    questObject.add("subquests", subQuestObject);
                    return questObject;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    /**
     * Serializes the completed sub-quests of a user into a JSON array.
     *
     * @param user The user whose completed sub-quests should be serialized.
     * @return A JSON array containing the serialized completed sub-quests.
     */
    public static JsonArray serializeCompletedSubquests(User user) {
        return user.getCompletedSubQuests().stream()
                .map(subQuest -> {
                    JsonObject questObject = new JsonObject();
                    questObject.addProperty("subquest-id", subQuest.getId());
                    return questObject;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    /**
     * Serializes the cooldown sub-quests of a user into a JSON array.
     *
     * @param user The user whose cooldown sub-quests should be serialized.
     * @return A JSON array containing the serialized cooldown sub-quests.
     */
    public static JsonArray serializeCooldownSubQuests(User user) {
        return user.getCooldownSubQuests().entrySet().stream()
                .map(entry -> {
                    JsonObject subQuestObject = new JsonObject();
                    subQuestObject.addProperty("subquest-id", entry.getKey().getId());
                    subQuestObject.addProperty("cooldown", entry.getValue().toString());
                    return subQuestObject;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    /**
     * Serializes the chance modifiers of a user into a JSON array.
     *
     * @param user The user whose chance modifiers should be serialized.
     * @return A JSON array containing the serialized chance modifiers.
     */
    public static JsonArray serializeChanceModifiers(User user) {
        return user.getChanceModifiers().entrySet().stream()
                .map(entry -> {
                    JsonObject subQuestObject = new JsonObject();
                    subQuestObject.addProperty("subquest-id", entry.getKey().getId());
                    subQuestObject.addProperty("chance-modifier", entry.getValue().toString());
                    return subQuestObject;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    /**
     * Deserializes the progress of a user from a JSON array.
     *
     * @param progressJson The JSON array containing the serialized progress.
     * @return A LinkedHashMap mapping sub-quests to their progress.
     */
    public static LinkedHashMap<SubQuest, AtomicInteger> deserializeProgress(JsonArray progressJson) {
        LinkedHashMap<SubQuest, AtomicInteger> map = new LinkedHashMap<>();
        for (JsonElement jsonElement : progressJson) {
            if (jsonElement.isJsonObject()) {
                JsonObject questObject = jsonElement.getAsJsonObject();
                int subQuestId = questObject.getAsJsonPrimitive("subquest-id").getAsInt();
                int progress = questObject.getAsJsonPrimitive("progress").getAsInt();

                // Retrieve the corresponding SubQuest and update user progress
                SubQuest subQuest = SubQuest.getById(subQuestId); // Implement getById method as needed
                if (subQuest != null) {
                    map.put(subQuest, new AtomicInteger(progress));
                }
            }
        }
        return map;
    }

    /**
     * Deserializes the active sub-quests of a user from a JSON array.
     *
     * @param activeSubQuestsJson The JSON array containing the serialized active sub-quests.
     * @return A LinkedHashMap mapping quests to their active sub-quests.
     */
    public static LinkedHashMap<Quest, SubQuest> deserializeActiveSubQuests(JsonArray activeSubQuestsJson) {
        LinkedHashMap<Quest, SubQuest> map = new LinkedHashMap<>();
        for (JsonElement jsonElement : activeSubQuestsJson) {
            if (jsonElement.isJsonObject()) {
                JsonObject questObject = jsonElement.getAsJsonObject();
                int questId = questObject.getAsJsonPrimitive("quest-id").getAsInt();

                if (questObject.has("subquests")) {
                    JsonObject subQuestObject = questObject.getAsJsonObject("subquests");
                    int subQuestId = subQuestObject.getAsJsonPrimitive("subquest-id").getAsInt();

                    // Retrieve the corresponding Quest and SubQuest and update user's active sub-quests
                    Quest quest = Quest.getById(questId); // Implement getById method as needed
                    SubQuest subQuest = SubQuest.getById(subQuestId); // Implement getById method as needed

                    if (quest != null && subQuest != null) {
                        map.put(quest, subQuest);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Deserializes the completed sub-quests of a user from a JSON array.
     *
     * @param completedSubquestsJson The JSON array containing the serialized completed sub-quests.
     * @return A LinkedList containing the completed sub-quests.
     */
    public static LinkedList<SubQuest> deserializeCompletedSubquests(JsonArray completedSubquestsJson) {
        LinkedList<SubQuest> list = new LinkedList<>();
        for (JsonElement jsonElement : completedSubquestsJson) {
            if (jsonElement.isJsonObject()) {
                JsonObject questObject = jsonElement.getAsJsonObject();
                int subQuestId = questObject.getAsJsonPrimitive("subquest-id").getAsInt();

                // Retrieve the corresponding SubQuest and add it to user's completed sub-quests
                SubQuest subQuest = SubQuest.getById(subQuestId); // Implement getById method as needed
                if (subQuest != null) {
                    list.add(subQuest);
                }
            }
        }
        return list;
    }

    /**
     * Deserializes the cooldown sub-quests of a user from a JSON array.
     *
     * @param cooldownSubquestsJson The JSON array containing the serialized cooldown sub-quests.
     * @return A LinkedHashMap mapping sub-quests to their cooldown end times.
     */
    public static LinkedHashMap<SubQuest, Instant> deserializeCooldownSubQuests(JsonArray cooldownSubquestsJson) {
        LinkedHashMap<SubQuest, Instant> cooldownSubQuests = new LinkedHashMap<>();
        for (JsonElement jsonElement : cooldownSubquestsJson) {
            if (jsonElement.isJsonObject()) {
                JsonObject subQuestObject = jsonElement.getAsJsonObject();
                int subQuestId = subQuestObject.getAsJsonPrimitive("subquest-id").getAsInt();
                String cooldownEndTimeStr = subQuestObject.getAsJsonPrimitive("cooldown").getAsString();

                SubQuest subQuest = SubQuest.getById(subQuestId); // Implement getById method as needed
                if (subQuest != null) {
                    Instant cooldownEndTime = Instant.parse(cooldownEndTimeStr);
                    cooldownSubQuests.put(subQuest, cooldownEndTime);
                }
            }
        }
        return cooldownSubQuests;
    }

    /**
     * Deserializes the chance modifiers of a user from a JSON array.
     *
     * @param json The JSON array containing the serialized chance modifiers.
     * @return A LinkedHashMap mapping Quest objects to their associated chance modifiers.
     */
    public static LinkedHashMap<Quest, Double> deserializeChanceModifiers(JsonArray json) {
        LinkedHashMap<Quest, Double> chanceModifiers = new LinkedHashMap<>();
        for (JsonElement element : json) {
            if (element.isJsonObject()) {
                JsonObject subQuestObject = element.getAsJsonObject();
                int subQuestId = subQuestObject.get("subquest-id").getAsInt();

                // Retrieve the corresponding Quest and SubQuest and update user's active sub-quests
                Quest quest = Quest.getById(subQuestId); // Assuming Quest has a constructor that takes an ID

                if (quest != null) {
                    double chanceModifier = Double.parseDouble(subQuestObject.get("chance-modifier").getAsString());
                    chanceModifiers.put(quest, chanceModifier);
                }
            }
        }
        return chanceModifiers;
    }
}
