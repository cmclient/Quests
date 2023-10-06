package pl.kuezese.quests.helper;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for creating custom ItemStacks with specified Material, amount, name, lore, and enchantments.
 */
public class ItemMaker {

    private final Material mat;
    private final int amount;
    private final short data;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;

    /**
     * Creates an ItemMaker from an existing ItemStack.
     *
     * @param stack The ItemStack to create an ItemMaker from.
     */
    public ItemMaker(ItemStack stack) {
        this(stack.getType(), stack.getAmount(), stack.getDurability());
        if (stack.hasItemMeta()) {
            this.name = stack.getItemMeta().getDisplayName();
            if (stack.getItemMeta().getLore() != null) {
                this.lore = stack.getItemMeta().getLore();
            }
        }
    }

    /**
     * Creates an ItemMaker with the specified Material and default amount of 1.
     *
     * @param mat The Material of the ItemStack.
     */
    public ItemMaker(Material mat) {
        this(mat, 1);
    }

    /**
     * Creates an ItemMaker with the specified Material and amount.
     *
     * @param mat    The Material of the ItemStack.
     * @param amount The amount of the ItemStack.
     */
    public ItemMaker(Material mat, int amount) {
        this(mat, amount, (short) 0);
    }

    /**
     * Creates an ItemMaker with the specified Material, amount, and data (durability).
     *
     * @param mat    The Material of the ItemStack.
     * @param amount The amount of the ItemStack.
     * @param data   The data (durability) of the ItemStack.
     */
    public ItemMaker(Material mat, int amount, short data) {
        this.mat = mat;
        this.amount = amount;
        this.data = data;
        this.name = null;
    }

    /**
     * Sets the name of the ItemStack.
     *
     * @param name The name to set for the ItemStack.
     * @return This ItemMaker instance for method chaining.
     */
    public ItemMaker setName(String name) {
        this.name = ChatHelper.format(name);
        return this;
    }

    /**
     * Adds a line of lore to the ItemStack.
     *
     * @param s The lore line to add.
     * @return This ItemMaker instance for method chaining.
     */
    public ItemMaker addLore(String s) {
        if (this.lore == null)
            this.lore = new ArrayList<>();
        this.lore.add(ChatHelper.format(s));
        return this;
    }

    /**
     * Adds an enchantment to the ItemStack.
     *
     * @param enchant The enchantment to add.
     * @param level   The level of the enchantment.
     * @return This ItemMaker instance for method chaining.
     */
    public ItemMaker addEnchant(Enchantment enchant, int level) {
        if (this.enchants == null)
            this.enchants = new HashMap<>();
        this.enchants.put(enchant, level);
        return this;
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param lore The list of lore lines to set.
     * @return This ItemMaker instance for method chaining.
     */
    public ItemMaker setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Creates the final ItemStack based on the specified attributes (Material, amount, name, lore, and enchantments).
     *
     * @return The created ItemStack.
     */
    public ItemStack make() {
        ItemStack item = new ItemStack(this.mat, this.amount, this.data);
        ItemMeta meta = item.getItemMeta();
        if (this.name != null) {
            meta.setDisplayName(this.name);
        }
        if (this.lore != null && !this.lore.isEmpty()) {
            meta.setLore(this.lore);
        }
        item.setItemMeta(meta);
        if (this.enchants != null) {
            item.addUnsafeEnchantments(this.enchants);
        }
        return item;
    }
}
