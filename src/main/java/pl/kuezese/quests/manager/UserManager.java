package pl.kuezese.quests.manager;

import com.google.gson.JsonParser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;
import pl.kuezese.quests.serializer.UserSerializer;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Manages user-related operations, such as finding or creating user profiles.
 */
public @Getter class UserManager {

    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>(); // Collection to store user profiles.

    /**
     * Find a user profile by their UUID.
     *
     * @param uuid The UUID of the user to find.
     * @return The user profile if found, or null if not found.
     */
    public @Nullable User find(UUID uuid) {
        return this.users.get(uuid);
    }

    /**
     * Find or create a user profile by their UUID.
     *
     * @param uuid The UUID of the user to find or create.
     * @return The existing or newly created user profile.
     */
    public @NotNull User findOrCreate(UUID uuid) {
        User user = this.users.get(uuid);

        // If the user does not exist, create a new user profile.
        if (user == null) {
            user = new User(uuid);
            user.insert();
            this.users.put(uuid, user);
        }

        return user;
    }

    /**
     * Load user data from a database.
     *
     * @param quests The Quests plugin instance.
     */
    public void load(Quests quests) {
        quests.getMySQLDatabase().query("SELECT * FROM `users`", rs -> {
            try {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    LinkedHashMap<SubQuest, AtomicInteger> progress = UserSerializer.deserializeProgress(JsonParser.parseString(rs.getString("progress")).getAsJsonArray());
                    LinkedHashMap<Quest, SubQuest> activeSubquests = UserSerializer.deserializeActiveSubQuests(JsonParser.parseString(rs.getString("active")).getAsJsonArray());
                    LinkedList<SubQuest> completedSubquests = UserSerializer.deserializeCompletedSubquests(JsonParser.parseString(rs.getString("completed")).getAsJsonArray());

                    User user = new User(uuid, progress, activeSubquests, completedSubquests, Instant.now());
                    this.users.put(user.getUuid(), user);
                }
            } catch (Throwable ex) {
                quests.getLogger().log(Level.WARNING, "Error while loading users from database!", ex);
            }
            quests.getLogger().info("Loaded " + users.size() + " players");
        });
    }
}
