package pl.kuezese.quests.database;

import lombok.Getter;
import pl.kuezese.quests.Quests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public @Getter class MySQLDatabase {

    private final ExecutorService executor = Executors.newScheduledThreadPool(10);
    private Connection connection;

    public boolean connect(Quests quests) {
        try {
            quests.getLogger().info("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + quests.getConfiguration().mysqlHost + ":" + quests.getConfiguration().mysqlPort + "/" + quests.getConfiguration().mysqlDatabase, quests.getConfiguration().mysqlUser, quests.getConfiguration().mysqlPassword);
            quests.getServer().getScheduler().runTaskTimer(quests, () -> this.execute("SELECT CURTIME()"), 15000L, 15000L);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            quests.getLogger().warning("Unable to connect to database. Error: " + e.getMessage());
            return false;
        }
    }

    public void execute(String query) {
        executor.submit(() -> {
            try {
                this.connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(String update) {
        executor.submit(() -> {
            try {
                connection.createStatement().executeUpdate(update);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(String update, boolean async) {
        if (async) {
            this.update(update);
            return;
        }

        try {
            connection.createStatement().executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void query(String query, QueryCallback callback) {
        executor.submit(() -> {
            try (ResultSet rs = connection.createStatement().executeQuery(query)) {
                callback.received(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public interface QueryCallback {
        void received(ResultSet rs);
    }
}