package pl.kuezese.quests.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import pl.kuezese.quests.Quests;

/**
 * Represents the configuration for the Quests plugin.
 */
public @Getter class Configuration {

    private final Quests quests;

    /**
     * Constructs a new Configuration instance.
     *
     * @param quests The Quests plugin instance.
     */
    public Configuration(Quests quests) {
        this.quests = quests;
        this.reload();
    }

    /**
     * Reloads the configuration from the plugin's config file.
     */
    public void reload() {
        // Save the default configuration if it doesn't exist
        this.quests.saveDefaultConfig();

        // Reload the configuration from the file
        this.quests.reloadConfig();

        // Get the FileConfiguration instance
        final FileConfiguration config = this.quests.getConfig();

        // Load MySQL configuration values from the config file
        this.mysqlHost = config.getString("database.host");
        this.mysqlPort = config.getInt("database.port");
        this.mysqlDatabase = config.getString("database.database");
        this.mysqlUser = config.getString("database.username");
        this.mysqlPassword = config.getString("database.password");
    }

    /**
     * The MySQL host.
     */
    private String mysqlHost;

    /**
     * The MySQL database name.
     */
    private String mysqlDatabase;

    /**
     * The MySQL username.
     */
    private String mysqlUser;

    /**
     * The MySQL password.
     */
    private String mysqlPassword;

    /**
     * The MySQL port.
     */
    private int mysqlPort;
}
