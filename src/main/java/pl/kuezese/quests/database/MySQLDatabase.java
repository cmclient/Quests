package pl.kuezese.quests.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.kuezese.quests.Quests;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public @Getter @RequiredArgsConstructor class MySQLDatabase {

    private final Quests quests;
    private Connection connection;
    private final ExecutorService executor = Executors.newScheduledThreadPool(10);

    public boolean connect(Quests quests) {
        try {
            quests.getLogger().info("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + quests.getConfiguration().mysqlHost + ":" + quests.getConfiguration().mysqlPort + "/" + quests.getConfiguration().mysqlDatabase, quests.getConfiguration().mysqlUser, quests.getConfiguration().mysqlPassword);
            quests.getServer().getScheduler().runTaskTimerAsynchronously(quests, () -> this.execute("SELECT CURTIME()"), 15000L, 15000L);
            return true;
        } catch (SQLException | ClassNotFoundException ex) {
            quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
            return false;
        }
    }

    public void execute(String query) {
        try {
            this.connection.createStatement().execute(query);
        } catch (SQLException ex) {
            this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
        }
    }

    public void update(String update) {
        this.executor.submit(() -> {
            try {
                this.connection.createStatement().executeUpdate(update);
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
            }
        });
    }

    public void update(PreparedStatement update) {
        this.executor.submit(() -> {
            try {
                update.executeUpdate();
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
            }
        });
    }

    public void update(String update, boolean async) {
        if (async) {
            this.update(update);
            return;
        }

        try {
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException ex) {
            this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
        }
    }

    public void query(String query, QueryCallback callback) {
        this.executor.submit(() -> {
            try (ResultSet rs = connection.createStatement().executeQuery(query)) {
                callback.accept(rs);
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
            }
        });
    }

    public void update(PreparedStatement update, Runnable callback) {
        this.executor.submit(() -> {
            try {
                update.executeUpdate();
                callback.run();
            } catch (SQLException ex) {
                this.quests.getLogger().log(Level.WARNING, "MySQL Error!", ex);
            }
        });
    }

    public interface QueryCallback {
        void accept(ResultSet rs);
    }
}