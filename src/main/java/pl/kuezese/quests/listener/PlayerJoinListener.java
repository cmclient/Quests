package pl.kuezese.quests.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.object.User;

/**
 * Listener for player join and quit events, responsible for initializing and saving user data.
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final Quests quests; // The Quests plugin instance.

    /**
     * Handles the PlayerJoinEvent.
     *
     * @param event The PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = this.quests.getUserManager().findOrCreate(player.getUniqueId());

        // Synchronize user data.
        user.synchronize(player);
    }

    /**
     * Handles the PlayerQuitEvent.
     *
     * @param event The PlayerQuitEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.quests.getUserManager().find(player.getUniqueId(), User::update);
    }
}
