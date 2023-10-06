package pl.kuezese.quests.helper;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

/**
 * A utility class for filling inventory slots with empty glass panes.
 */
public final class InventoryHelper {

    // A constant ItemStack representing an empty glass pane.
    private static final ItemStack GLASS = new ItemMaker(Material.BLACK_STAINED_GLASS_PANE).setName(" ").make();

    /**
     * Fills empty slots in an inventory with empty glass panes.
     *
     * @param inventory The inventory to fill.
     */
    public static void fill(Inventory inventory) {
        // Use IntStream to iterate through the inventory slots.
        IntStream.range(0, inventory.getSize())
                .filter(i -> inventory.getItem(i) == null) // Filter empty slots.
                .forEach(i -> inventory.setItem(i, GLASS)); // Set the empty glass pane.
    }
}
