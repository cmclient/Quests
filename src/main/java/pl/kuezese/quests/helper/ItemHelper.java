package pl.kuezese.quests.helper;

import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.inventory.ItemStack;

/**
 * A utility class for working with items.
 */
public final class ItemHelper {

    /**
     * Checks if an ItemStack is not air.
     *
     * @param item The ItemStack to check.
     * @return True if the ItemStack is not air, false otherwise.
     */
    public static boolean isNotAir(ItemStack item) {
        return switch (item.getType()) {
            case AIR, CAVE_AIR, VOID_AIR -> false;
            default -> true;
        };
    }

    /**
     * Checks if an ItemStack is a valid MMOItem.
     *
     * @param itemStack The ItemStack to check.
     * @param mmoItem   The MMOItem to compare with.
     * @return True if the ItemStack is a valid MMOItem, false otherwise.
     */
    public static boolean isValidItem(ItemStack itemStack, MMOItem mmoItem) {
        MMOItemReforger mmoItemReforger = new MMOItemReforger(itemStack);
        return mmoItemReforger.hasTemplate() &&
                !"VANILLA".equals(mmoItemReforger.getNBTItem().getString("MMOITEMS_ITEM_ID")) &&
                mmoItem.getType() == mmoItemReforger.getTemplate().getType() &&
                mmoItem.getId().equals(mmoItemReforger.getTemplate().getId());
    }
}
