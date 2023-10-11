package pl.kuezese.quests.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.kuezese.quests.Quests;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Represents a MySQL database connection manager for the Quests plugin.
 */
public @Getter @RequiredArgsConstructor class MySQLDatabase {

    private final Quests quests;
    private Connection connection;
    private final ExecutorService executor = Executors.newScheduledThreadPool(10);

    /**
     * Attempts to establish a connection to the MySQL database.
     *
     * @param quests The Quests plugin instance.
     * @return True if the connection was successful, false otherwise.
     */
    public boolean connect(Quests quests) {
        try {
            quests.getLogger().info("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + quests.getConfiguration().getMysqlHost() + ":" + quests.getConfiguration().getMysqlPort() + "/" + quests.getConfiguration().getMysqlDatabase(), quests.getConfiguration().getMysqlUser(), quests.getConfiguration().getMysqlPassword());

            // Schedule a recurring task to execute a test query every 15 seconds
            quests.getServer().getScheduler().runTaskTimerAsynchronously(quests, () -> this.execute("SELECT CURTIME()"), 15000L, 15000L);
            return true;
        } catch (SQLException | ClassNotFoundException ex) {
            quests.getLogger().log(Level.WARNING, "Failed to connect to MySQL server!", ex);
            return false;
        }
    }

    /**
     * Executes a SQL query on the connected MySQL database.
     *
     * @param query The SQL query to execute.
     */
    public void execute(String query) {
        try {
            this.connection.createStatement().execute(query);
        } catch (SQLException ex) {
            this.quests.getLogger().log(Level.WARNING, "Failed to execute MySQL statement!", ex);
        }
    }

    /**
     * Submits an SQL update query to be executed asynchronously.
     *
     * @param update The SQL update query to execute.
     */
    public void update(String update) {
        this.executor.submit(() -> {
            try {
                this.connection.createStatement().executeUpdate(update);
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "Failed to create and update MySQL statement!", ex);
            }
        });
    }

    /**
     * Submits a prepared SQL update query to be executed asynchronously.
     *
     * @param update The prepared SQL update query to execute.
     */
    public void update(PreparedStatement update) {
        this.executor.submit(() -> {
            try {
                update.executeUpdate();
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "Failed to update prepared MySQL statement!", ex);
            }
        });
    }

    /**
     * Submits an SQL update query to be executed either asynchronously or synchronously.
     *
     * @param update The SQL update query to execute.
     * @param async  True to execute asynchronously, false to execute synchronously.
     */
    public void update(String update, boolean async) {
        if (async) {
            this.update(update);
            return;
        }

        try {
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException ex) {
            this.quests.getLogger().log(Level.WARNING, "Failed to create and update MySQL statement!", ex);
        }
    }

    /**
     * Executes an SQL query on the connected MySQL database and handles the result with a callback.
     *
     * @param query    The SQL query to execute.
     * @param callback A callback function to handle the ResultSet.
     */
    public void query(String query, QueryCallback callback) {
        this.executor.submit(() -> {
            try (ResultSet rs = connection.createStatement().executeQuery(query)) {
                callback.accept(rs);
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "Failed to create and query MySQL statement!", ex);
            }
        });
    }

    /**
     * Executes a prepared SQL query on the connected MySQL database and handles the result with a callback.
     *
     * @param preparedStatement The prepared SQL query to execute.
     * @param callback           A callback function to handle the ResultSet.
     */
    public void query(PreparedStatement preparedStatement, QueryCallback callback) {
        try (ResultSet rs = preparedStatement.executeQuery()) {
            callback.accept(rs);
        } catch (SQLException ex) {
            this.quests.getLogger().log(Level.WARNING, "Failed to create and query MySQL statement!", ex);
        }
    }

    /**
     * A functional interface for handling query results.
     */
    public interface QueryCallback {
        /**
         * Accepts a ResultSet as a parameter for processing query results.
         *
         * @param rs The ResultSet containing query results.
         */
        void accept(ResultSet rs);
    }
}
