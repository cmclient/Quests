package pl.kuezese.quests.menu;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.helper.ChatHelper;
import pl.kuezese.quests.helper.InventoryHelper;
import pl.kuezese.quests.helper.ItemHelper;
import pl.kuezese.quests.helper.ItemMaker;
import pl.kuezese.quests.object.SubQuest;
import pl.kuezese.quests.object.User;
import pl.kuezese.quests.type.QuestAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Represents a menu for displaying information about a specific sub-quest.
 */
public class QuestMenu {

    private final Inventory inventory; // The inventory for displaying sub-quest information.

    /**
     * Constructs a new QuestMenu.
     *
     * @param quests    The Quests plugin instance.
     * @param subQuest The SubQuest to display information for.
     */
    public QuestMenu(Quests quests, SubQuest subQuest, Player player, User user) {
        this.inventory = quests.getServer().createInventory(null, 27, subQuest.getTitle());
        List<String> progress = new LinkedList<>(subQuest.getProgress());
        AtomicInteger questProgress = user.getProgress().getOrDefault(subQuest, new AtomicInteger());

        // Replace placeholders in progress messages.
        ChatHelper.replace(progress, "{PROGRESS}", String.valueOf(questProgress));
        ChatHelper.replace(progress, "{TOTAL}", String.valueOf(subQuest.getAmount()));

        // Check if sub-quest action is ITEM and calculate number of items in player inventory
        if (subQuest.getAction() == QuestAction.ITEM) {
            MMOItemTemplate mmoItemTemplate = MMOItems.plugin.getTemplates().getTemplateOrThrow(subQuest.getMmoItemType(), subQuest.getMmoItemId().toUpperCase().replace("-", "_"));
            MMOItem item = mmoItemTemplate.newBuilder().build();

            int amount = Stream.of(player.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .filter(ItemStack::hasItemMeta)
                    .filter(ItemHelper::isNotAir)
                    .filter(itemStack -> ItemHelper.isValidItem(itemStack, item))
                    .mapToInt(ItemStack::getAmount)
                    .sum();

            ChatHelper.replace(progress, "{AMOUNT}", String.valueOf(amount));
        }

        // Set items in the inventory.
        this.inventory.setItem(11, new ItemMaker(Material.ENDER_EYE).setName(" ").setLore(progress).make());
        this.inventory.setItem(13, new ItemMaker(Material.WRITABLE_BOOK).setName(" ").setLore(subQuest.getDescription()).make());

        if (user.getCompletedSubQuests().contains(subQuest)) {
            this.inventory.setItem(15, new ItemMaker(Material.TRIPWIRE_HOOK).setName(" ").setLore(subQuest.getAlreadyRedeemed()).make());
        } else if (questProgress.get() >= subQuest.getAmount()) {
            this.inventory.setItem(15, new ItemMaker(Material.TRIPWIRE_HOOK).setName(" ").setLore(subQuest.getRedeem()).make());
        } else {
            this.inventory.setItem(15, new ItemMaker(Material.TRIPWIRE_HOOK).setName(" ").setLore(subQuest.getCantRedeem()).make());
        }

        // Fill the remaining inventory slots with glass.
        InventoryHelper.fill(this.inventory);
    }

    /**
     * Opens the QuestMenu for a player and plays a sound.
     *
     * @param player The player for whom the menu should be opened.
     */
    public void open(Player player) {
        // Play a sound to indicate the menu opening.
        player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1.0f, 1.0f);
        // Open the inventory for the player.
        player.openInventory(this.inventory);
    }
}
