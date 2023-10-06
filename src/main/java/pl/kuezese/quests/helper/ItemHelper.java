package pl.kuezese.quests.helper;

import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.inventory.ItemStack;

public final class ItemHelper {

    public static boolean isNotAir(ItemStack item) {
        return switch (item.getType()) {
            case AIR, CAVE_AIR, VOID_AIR -> false;
            default -> true;
        };
    }

    public static boolean isValidItem(ItemStack itemStack, MMOItem mmoItem) {
        MMOItemReforger mmoItemReforger = new MMOItemReforger(itemStack);
        return mmoItemReforger.hasTemplate() &&
                !"VANILLA".equals(mmoItemReforger.getNBTItem().getString("MMOITEMS_ITEM_ID")) &&
                mmoItem.getType() == mmoItemReforger.getTemplate().getType() &&
                mmoItem.getId().equals(mmoItemReforger.getTemplate().getId());
    }
}
