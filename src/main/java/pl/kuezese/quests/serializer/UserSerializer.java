package pl.kuezese.quests.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserSerializer {

    /**
     * Serializes the progress of a user into a JSON array.
     *
     * @param user The user whose progress should be serialized.
     * @return A JSON array containing the serialized progress.
     */
    public static JsonArray serializeProgress(User user) {
        JsonArray json = new JsonArray();
        for (Map.Entry<SubQuest, AtomicInteger> entry : user.getProgress().entrySet()) {
            SubQuest subQuest = entry.getKey();
            AtomicInteger progress = entry.getValue();

            // Create a JSON object for the quest progress
            JsonObject questObject = new JsonObject();
            questObject.addProperty("subquest-id", subQuest.getId());
            questObject.addProperty("progress", progress.get());

            json.add(questObject);
        }
        return json;
    }

    /**
     * Serializes the active sub-quests of a user into a JSON array.
     *
     * @param user The user whose active sub-quests should be serialized.
     * @return A JSON array containing the serialized active sub-quests.
     */
    public static JsonArray serializeActiveSubQuests(User user) {
        JsonArray json = new JsonArray();
        for (Map.Entry<Quest, SubQuest> entry : user.getActiveSubQuests().entrySet()) {
            Quest quest = entry.getKey();
            SubQuest subQuest = entry.getValue();

            // Create a JSON object for the quest and its active sub-quest
            JsonObject questObject = new JsonObject();
            questObject.addProperty("quest-id", quest.getId());

            // Create a JSON object for the active sub-quest
            JsonObject subQuestObject = new JsonObject();
            subQuestObject.addProperty("subquest-id", subQuest.getId());

            questObject.add("subquests", subQuestObject);
            json.add(questObject);
        }
        return json;
    }

    /**
     * Serializes the completed sub-quests of a user into a JSON array.
     *
     * @param user The user whose completed sub-quests should be serialized.
     * @return A JSON array containing the serialized completed sub-quests.
     */
    public static JsonArray serializeCompletedSubquests(User user) {
        JsonArray json = new JsonArray();
        for (SubQuest subQuest : user.getCompletedSubQuests()) {
            // Create a JSON object for the completed sub-quest
            JsonObject questObject = new JsonObject();
            questObject.addProperty("subquest-id", subQuest.getId());
            json.add(questObject);
        }
        return json;
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
}
