package pl.kuezese.quests;

import lombok.Getter;
import lombok.Setter;
import net.Indyuce.mmoitems.api.MMOItemsAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kuezese.quests.config.Configuration;
import pl.kuezese.quests.database.MySQLDatabase;
import pl.kuezese.quests.listener.EntityDeathListener;
import pl.kuezese.quests.listener.EntityInteractListener;
import pl.kuezese.quests.listener.InventoryClickListener;
import pl.kuezese.quests.listener.PlayerJoinListener;
import pl.kuezese.quests.manager.QuestManager;
import pl.kuezese.quests.manager.UserManager;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * The main class of the Quests plugin.
 * This class extends JavaPlugin and handles plugin initialization and event registration.
 */
public final @Getter class Quests extends JavaPlugin {

    private static @Getter Quests instance;

    private Configuration configuration;
    private QuestManager questManager;
    private UserManager userManager;
    private MySQLDatabase mySQLDatabase;
    private NPCRegistry npcRegistry;
    private MMOItemsAPI mmoItemsAPI;
    private @Setter boolean loaded;

    /**
     * Constructor for the Quests class.
     * Initializes the instance variable.
     */
    public Quests() {
        instance = this;
    }

    /**
     * Called when the plugin is enabled. Performs plugin setup, command and listener registration, and task scheduling.
     */
    @Override
    public void onEnable() {
        // Load configuration
        this.configuration = new Configuration(this);

        // Register managers
        (this.questManager = new QuestManager()).load(this);
        this.userManager = new UserManager();

        // Register database
        this.mySQLDatabase = new MySQLDatabase(this);

        // Conncet to database and load data
        if (this.mySQLDatabase.connect(this)) {
            // Create the 'users' table if it doesn't exist
            this.mySQLDatabase.update("CREATE TABLE IF NOT EXISTS `users` (`uuid` varchar(36) PRIMARY KEY NOT NULL, `progress` LONGTEXT NOT NULL, `active` LONGTEXT NOT NULL, `completed` LONGTEXT NOT NULL);", false);
            // Load user data
            this.userManager.load(this);
        } else {
            // Shutdown the server if the database connection fails
            this.getServer().shutdown();
        }

        // Register 3rd party API
        this.npcRegistry = CitizensAPI.getNPCRegistry();
        this.mmoItemsAPI = new MMOItemsAPI(this);

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityInteractListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);

        this.setLoaded(true);
        this.getLogger().info(String.format("Plugin %s by %s has been loaded successfully.", this.getDescription().getFullName(), this.getDescription().getAuthors().get(0)));
    }

    /**
     * Called when the plugin is disabled. Performs cleanup or save operations if necessary.
     */
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        if (!this.isLoaded()) {
            this.getLogger().warning("Plugin " + getDescription().getFullName() + " by " + getDescription().getAuthors().get(0) + " failed to load!");
            this.getServer().shutdown();
            return;
        }

        this.getMySQLDatabase().getExecutor().shutdown();
        try {
            this.getMySQLDatabase().getExecutor().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            this.getLogger().log(Level.WARNING, "MySQL Error!", ex);
        }

        this.getLogger().info(String.format("Plugin %s by %s has been disabled successfully.", this.getDescription().getFullName(), this.getDescription().getAuthors().get(0)));
    }
}
