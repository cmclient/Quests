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

    public static JsonArray serializeProgress(User user) {
        JsonArray json = new JsonArray();
        for (Map.Entry<SubQuest, AtomicInteger> entry : user.getProgress().entrySet()) {
            SubQuest subQuest = entry.getKey();
            AtomicInteger progress = entry.getValue();

            // Create an Quest object
            JsonObject questObject = new JsonObject();
            questObject.addProperty("subquest-id", subQuest.getId());
            questObject.addProperty("progress", progress.get());

            json.add(questObject);
        }
        return json;
    }

    public static JsonArray serializeActiveSubQuests(User user) {
        JsonArray json = new JsonArray();
        for (Map.Entry<Quest, SubQuest> entry : user.getActiveSubQuests().entrySet()) {
            Quest quest = entry.getKey();
            SubQuest subQuest = entry.getValue();

            // Create an Quest object
            JsonObject questObject = new JsonObject();
            questObject.addProperty("quest-id", quest.getId());

            // Create an Subquest object
            JsonObject subQuestObject = new JsonObject();
            subQuestObject.addProperty("subquest-id", subQuest.getId());

            questObject.add("subquests", subQuestObject);
            json.add(questObject);
        }
        return json;
    }

    public static JsonArray serializeCompletedSubquests(User user) {
        JsonArray json = new JsonArray();
        for (SubQuest subQuest : user.getCompletedSubQuests()) {
            // Create an Quest object
            JsonObject questObject = new JsonObject();
            questObject.addProperty("subquest-id", subQuest.getId());
            json.add(questObject);
        }
        return json;
    }

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
