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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param json The JSON array containing the serialized progress.
     * @return A LinkedHashMap mapping sub-quests to their progress.
     */
    public static LinkedHashMap<SubQuest, AtomicInteger> deserializeProgress(JsonArray json) {
        return Stream.of(json)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(questObject -> questObject.has("subquest-id") && questObject.has("progress"))
                .collect(Collectors.toMap(
                        questObject -> SubQuest.getById(questObject.getAsJsonPrimitive("subquest-id").getAsInt()),
                        questObject -> new AtomicInteger(questObject.getAsJsonPrimitive("progress").getAsInt()),
                        (existing, replacement) -> {
                            existing.addAndGet(replacement.get());
                            return existing;
                        },
                        LinkedHashMap::new
                ));
    }

    /**
     * Deserializes the active sub-quests of a user from a JSON array.
     *
     * @param json The JSON array containing the serialized active sub-quests.
     * @return A LinkedHashMap mapping quests to their active sub-quests.
     */
    public static LinkedHashMap<Quest, SubQuest> deserializeActiveSubQuests(JsonArray json) {
        return Stream.of(json)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(questObject -> questObject.has("quest-id") && questObject.has("subquests"))
                .map(questObject -> questObject.getAsJsonObject("subquests"))
                .filter(subQuestObject -> subQuestObject.has("subquest-id"))
                .collect(Collectors.toMap(
                        questObject -> Quest.getById(questObject.getAsJsonPrimitive("quest-id").getAsInt()),
                        subQuestObject -> SubQuest.getById(subQuestObject.getAsJsonPrimitive("subquest-id").getAsInt()),
                        (existing, replacement) -> existing, // No need for merging since keys are unique
                        LinkedHashMap::new
                ));

    }

    /**
     * Deserializes the completed sub-quests of a user from a JSON array.
     *
     * @param json The JSON array containing the serialized completed sub-quests.
     * @return A LinkedList containing the completed sub-quests.
     */
    public static LinkedList<SubQuest> deserializeCompletedSubquests(JsonArray json) {
        return Stream.of(json)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(questObject -> questObject.has("subquest-id"))
                .map(questObject -> questObject.getAsJsonPrimitive("subquest-id").getAsInt())
                .map(SubQuest::getById)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Deserializes the cooldown sub-quests of a user from a JSON array.
     *
     * @param json The JSON array containing the serialized cooldown sub-quests.
     * @return A LinkedHashMap mapping sub-quests to their cooldown end times.
     */
    public static LinkedHashMap<SubQuest, Instant> deserializeCooldownSubQuests(JsonArray json) {
        return Stream.of(json)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(subQuestObject -> subQuestObject.has("subquest-id") && subQuestObject.has("cooldown"))
                .collect(Collectors.toMap(
                        subQuestObject -> SubQuest.getById(subQuestObject.getAsJsonPrimitive("subquest-id").getAsInt()),
                        subQuestObject -> Instant.parse(subQuestObject.getAsJsonPrimitive("cooldown").getAsString()),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * Deserializes the chance modifiers of a user from a JSON array.
     *
     * @param json The JSON array containing the serialized chance modifiers.
     * @return A LinkedHashMap mapping Quest objects to their associated chance modifiers.
     */
    public static LinkedHashMap<Quest, Double> deserializeChanceModifiers(JsonArray json) {
        return Stream.of(json)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(subQuestObject -> subQuestObject.has("subquest-id") && subQuestObject.has("chance-modifier"))
                .collect(Collectors.toMap(
                        subQuestObject -> Quest.getById(subQuestObject.get("subquest-id").getAsInt()),
                        subQuestObject -> Double.parseDouble(subQuestObject.get("chance-modifier").getAsString()),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
}
