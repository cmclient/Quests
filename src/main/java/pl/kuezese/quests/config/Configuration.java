package pl.kuezese.quests.config;

import org.bukkit.configuration.file.FileConfiguration;
import pl.kuezese.quests.Quests;

public class Configuration {

    private final Quests quests;

    public String mysqlHost, mysqlDatabase, mysqlUser, mysqlPassword;
    public int mysqlPort;

    public Configuration(Quests quests) {
        this.quests = quests;
        this.reload();
    }

    public void reload() {
        this.quests.saveDefaultConfig();
        this.quests.reloadConfig();

        final FileConfiguration config = this.quests.getConfig();

        this.mysqlHost = config.getString("database.host");
        this.mysqlPort = config.getInt("database.port");
        this.mysqlDatabase = config.getString("database.database");
        this.mysqlUser = config.getString("database.username");
        this.mysqlPassword = config.getString("database.password");
    }
}
