package pl.kuezese.quests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;
import pl.kuezese.quests.serializer.UserSerializer;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestsTest {

    public static void main(String[] args) {
        // Create active quests and subquests
        Quest quest1 = Quest.builder().id(4).subQuests(Collections.singletonList(SubQuest.builder().id(0).build())).build();
        Quest quest2 = Quest.builder().id(3).subQuests(Collections.singletonList(SubQuest.builder().id(1).build())).build();
        Quest quest3 = Quest.builder().id(0).subQuests(Collections.singletonList(SubQuest.builder().id(0).build())).build();

        // Create a User
        User user = new User(UUID.randomUUID());

        // Add active quests and subquests to the User
        user.getActiveSubQuests().put(quest1, quest1.getSubQuests().get(0));
        user.getActiveSubQuests().put(quest2, quest2.getSubQuests().get(0));
        user.getActiveSubQuests().put(quest3, quest3.getSubQuests().get(0));

        // Add progress to the User
        user.getProgress().put(quest1.getSubQuests().get(0), new AtomicInteger(10));
        user.getProgress().put(quest1.getSubQuests().get(0), new AtomicInteger(20));
        user.getProgress().put(quest1.getSubQuests().get(0), new AtomicInteger(30));

        // Add sompleted subquests to the User
        user.getCompletedSubQuests().add(SubQuest.builder().id(2).build());

        // Serialize active sub quests
        JsonArray activeSubQuests = UserSerializer.serializeActiveSubQuests(user);

        // Serialize subquests progress
        JsonArray progress = UserSerializer.serializeProgress(user);

        // Serialize completed subquests
        JsonArray completedSubquests = UserSerializer.serializeCompletedSubquests(user);

        // Convert the custom JSON to a string and print it
        System.out.println(new Gson().toJson(activeSubQuests));
        System.out.println(new Gson().toJson(progress));
        System.out.println(new Gson().toJson(completedSubquests));
    }
}
