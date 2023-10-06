package pl.kuezese.quests.object;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.database.MySQLDatabase;
import pl.kuezese.quests.serializer.UserSerializer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Represents a user in the quest system, including their progress, active sub-quests, and completed sub-quests.
 */
public @Getter @Setter @RequiredArgsConstructor @AllArgsConstructor class User {

    private final UUID uuid; // The UUID of the user.
    private LinkedHashMap<SubQuest, AtomicInteger> progress = new LinkedHashMap<>(); // User's progress on sub-quests.
    private LinkedHashMap<Quest, SubQuest> activeSubQuests = new LinkedHashMap<>(); // Active sub-quests for the user.
    private LinkedList<SubQuest> completedSubQuests = new LinkedList<>(); // Completed sub-quests for the user.
    private transient Instant lastSynchronize = Instant.now(); // The timestamp of the last synchronization.

    /**
     * Synchronizes the user's data and updates the last synchronization timestamp.
     */
    public void synchronize() {
        this.lastSynchronize = Instant.now();
        Quests.getInstance().getServer().getScheduler().runTaskLater(Quests.getInstance(), this::download, 40L);
    }

    /**
     * Downloads the latest user data from MySQL and updates the user's progress, active subquests, and completed subquests.
     * This method should be called during synchronization to ensure the user's data is up to date.
     */
    public void download() {
        // Get the MySQLDatabase instance from Quests
        MySQLDatabase mySQLDatabase = Quests.getInstance().getMySQLDatabase();

        // Define the SQL SELECT statement to retrieve user data
        String sql = "SELECT progress, active, completed FROM users WHERE uuid = ?";

        try (PreparedStatement preparedStatement = mySQLDatabase.getConnection().prepareStatement(sql)) {
            // Set the UUID as a parameter for the prepared statement
            preparedStatement.setString(1, this.uuid.toString());

            // Execute the SELECT statement to retrieve the result set and check if a result was found
            mySQLDatabase.query(preparedStatement, (rs) -> {
                try {
                    if (rs.next()) {
                        // Update user's data based on the retrieved information
                        this.progress = UserSerializer.deserializeProgress(JsonParser.parseString(rs.getString("progress")).getAsJsonArray());
                        this.activeSubQuests = UserSerializer.deserializeActiveSubQuests(JsonParser.parseString(rs.getString("active")).getAsJsonArray());
                        this.completedSubQuests = UserSerializer.deserializeCompletedSubquests(JsonParser.parseString(rs.getString("completed")).getAsJsonArray());
                        this.lastSynchronize = Instant.now().minusSeconds(5);
                    }
                } catch (Exception ex) {
                    Quests.getInstance().getLogger().log(Level.WARNING, "Failed to download user data: " + this.uuid.toString() + "!", ex);
                }
            });
        } catch (Exception ex) {
            Quests.getInstance().getLogger().log(Level.WARNING, "Failed to download user data: " + this.uuid.toString() + "!", ex);
        }
    }

    /**
     * Inserts the user into database.
     */
    public void insert() {
        // Get the MySQLDatabase instance from Quests
        MySQLDatabase mySQLDatabase = Quests.getInstance().getMySQLDatabase();

        // Define the SQL INSERT statement
        String sql = "INSERT INTO users (uuid, progress, active, completed) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = mySQLDatabase.getConnection().prepareStatement(sql)) {
            // Set the values for the prepared statement
            preparedStatement.setString(1, this.uuid.toString());
            preparedStatement.setString(2, new Gson().toJson(UserSerializer.serializeProgress(this)));
            preparedStatement.setString(3, new Gson().toJson(UserSerializer.serializeActiveSubQuests(this)));
            preparedStatement.setString(4, new Gson().toJson(UserSerializer.serializeCompletedSubquests(this)));

            // Execute the INSERT statement
            mySQLDatabase.update(preparedStatement);
        } catch (SQLException ex) {
            Quests.getInstance().getLogger().log(Level.WARNING, "Failed to insert user: " + this.uuid.toString() + "!", ex);
        }
    }

    /**
     * Updates the user's data in the database.
     */
    public void update() {
        // Get the MySQLDatabase instance from Quests
        MySQLDatabase mySQLDatabase = Quests.getInstance().getMySQLDatabase();

        // Define the SQL UPDATE statement
        String sql = "UPDATE users SET progress=?, active=?, completed=? WHERE uuid=?";

        try (PreparedStatement preparedStatement = mySQLDatabase.getConnection().prepareStatement(sql)) {
            // Set the values for the prepared statement
            preparedStatement.setString(1, new Gson().toJson(UserSerializer.serializeProgress(this)));
            preparedStatement.setString(2, new Gson().toJson(UserSerializer.serializeActiveSubQuests(this)));
            preparedStatement.setString(3, new Gson().toJson(UserSerializer.serializeCompletedSubquests(this)));
            preparedStatement.setString(4, this.uuid.toString()); // Match by UUID

            // Execute the UPDATE statement
            mySQLDatabase.update(preparedStatement);
        } catch (SQLException ex) {
            Quests.getInstance().getLogger().log(Level.WARNING, "Failed to update user: " + this.uuid.toString() + "!", ex);
        }
    }

    /**
     * Checks if the user is synchronized (has waited for a certain duration after joining).
     *
     * @return True if the user is synchronized, false otherwise.
     */
    public boolean isSynchronized() {
        Instant currentTime = Instant.now();
        Duration timeElapsed = Duration.between(this.lastSynchronize, currentTime);
        return timeElapsed.getSeconds() >= 5;
    }
}
