package pl.kuezese.quests.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.object.User;
import pl.kuezese.quests.type.QuestAction;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener for entity death events, tracking mob kills for relevant sub-quests.
 */
@RequiredArgsConstructor
public class EntityDeathListener implements Listener {

    private final Quests quests; // The Quests plugin instance.

    /**
     * Handles entity death events to track mob kills for relevant sub-quests.
     *
     * @param event The EntityDeathEvent to handle.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMobKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        // Check if the killer is a player.
        if (killer != null) {
            User user = this.quests.getUserManager().findOrCreate(killer.getUniqueId());

            // Increment progress for sub-quests related to killing mobs.
            user.getActiveSubQuests()
                    .values()
                    .stream()
                    .filter(subQuest -> subQuest.getAction() == QuestAction.MOB)
                    .filter(subQuest -> subQuest.getMobName().equals(entity.getName()))
                    .forEach(subQuest -> {
                        AtomicInteger ai = user.getProgress().get(subQuest);

                        // If progress doesn't exist, create and initialize it.
                        if (ai == null) {
                            ai = new AtomicInteger(0);
                            user.getProgress().put(subQuest, ai);
                        }

                        // Increment progress count.
                        if (ai.get() < subQuest.getAmount()) {
                            ai.incrementAndGet();
                        }
                    });
        }
    }
}