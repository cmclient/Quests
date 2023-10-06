package pl.kuezese.quests.listener;

import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.helper.ChatHelper;
import pl.kuezese.quests.menu.QuestMenu;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

/**
 * Listener for player interactions with entities, such as NPCs, to trigger quests and sub-quests.
 */
@RequiredArgsConstructor
public class EntityInteractListener implements Listener {

    private final Quests quests; // The Quests plugin instance.

    /**
     * Handles player interactions with entities.
     *
     * @param event The PlayerInteractEntityEvent to handle.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        Entity entity = event.getRightClicked();

        // Check if the entity is an NPC.
        if (this.quests.getNpcRegistry().isNPC(entity)) {
            NPC npc = this.quests.getNpcRegistry().getNPC(entity);
            Quest quest = this.quests.getQuestManager().findQuestById(npc.getId());

            if (quest != null) {
                Player player = event.getPlayer();
                User user = this.quests.getUserManager().findOrCreate(player.getUniqueId());

                // Check if user is synchronized
                if (!user.isSynchronized()) {
                    Instant currentTime = Instant.now();
                    Duration timeElapsed = Duration.between(user.getLastSynchronize(), currentTime);
                    int timeLeft = (int) Math.max(0, 5 - timeElapsed.getSeconds());

                    player.sendMessage(ChatHelper.format(" &8» &7Zaczekaj na synchronizację danych. Pozostało &3" + timeLeft + "s&7."));
                    return;
                }

                LinkedList<SubQuest> subQuests = new LinkedList<>(quest.getSubQuests());

                // If there are no sub-quests for this quest, do nothing.
                if (subQuests.isEmpty())
                    return;

                // Remove completed sub-quests from the list.
                subQuests.removeIf(subQuest -> user.getCompletedSubQuests().contains(subQuest));

                // If all sub-quests are completed, open the menu for the last sub-quest.
                if (subQuests.isEmpty()) {
                    QuestMenu menu = new QuestMenu(this.quests, quest.getSubQuests().get(quest.getSubQuests().size() - 1), player, user);
                    menu.open(player);
                    return;
                }

                // Get the next active sub-quest for the user, and open the menu.
                SubQuest subQuest = user.getActiveSubQuests().get(quest);

                if (subQuest == null) {
                    subQuest = subQuests.get(0);

                    if (subQuest.getMmoCoreRequiredLvl() > this.quests.getMmoItemsAPI().getPlayerData(player).getRPG().getLevel()) {
                        subQuest.getNoRequiredLvl().forEach(player::sendMessage);
                        return;
                    }

                    subQuest.getFirstInteraction().forEach(player::sendMessage);
                    user.getActiveSubQuests().put(quest, subQuest);
                }

                // Create and open sub-quest menu for the player.
                QuestMenu menu = new QuestMenu(this.quests, subQuest, player, user);
                menu.open(player);
            }
        }
    }
}
