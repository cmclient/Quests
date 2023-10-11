package pl.kuezese.quests.listener;

import lombok.RequiredArgsConstructor;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.helper.ChatHelper;
import pl.kuezese.quests.helper.ItemHelper;
import pl.kuezese.quests.menu.QuestMenu;
import pl.kuezese.quests.object.Quest;
import pl.kuezese.quests.object.User;
import pl.kuezese.quests.type.QuestAction;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Listener for inventory click events, responsible for preventing interactions in specific sub-quest menus.
 */
@RequiredArgsConstructor
public class InventoryClickListener implements Listener {

    private final Quests quests; // The Quests plugin instance.

    /**
     * Handles inventory click events.
     *
     * @param event The InventoryClickEvent to handle.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // Check if the clicked inventory title matches any sub-quest title, and cancel the event if it does.
        this.quests.getQuestManager().getQuests().values().stream().map(Quest::getSubQuests)
                .forEach(subQuests -> subQuests.stream().filter(subQuest -> subQuest.getTitle().equalsIgnoreCase(event.getView().getTitle()))
                        .findAny()
                        .ifPresent(subQuest -> {
                            event.setCancelled(true);
                            User user = this.quests.getUserManager().findOrCreate(player.getUniqueId());

                            // Logic for giving the item to the NPC.
                            if (event.getSlot() == 11 && subQuest.getAction() == QuestAction.ITEM) {
                                if (user.getCompletedSubQuests().contains(subQuest)) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &cJuz zakonczyles tego questa&7."));
                                    player.closeInventory();
                                    return;
                                }

                                Type mmoItemType = subQuest.getMmoItemType();
                                String mmoItemId = subQuest.getMmoItemId();

                                MMOItemTemplate mmoItemTemplate = MMOItems.plugin.getTemplates().getTemplateOrThrow(mmoItemType, mmoItemId.toUpperCase().replace("-", "_"));
                                MMOItem item = mmoItemTemplate.newBuilder().build();

                                // Reference of progress to access in lambda
                                var ref = new Object() {
                                    AtomicInteger ai = user.getProgress().get(subQuest);
                                };

                                // If progress doesn't exist, create and initialize it.
                                if (ref.ai == null) {
                                    ref.ai = new AtomicInteger(0);
                                    user.getProgress().put(subQuest, ref.ai);
                                }

                                if (ref.ai.get() >= subQuest.getAmount()) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &eJuz oddales wszystkie potrzebne przedmioty&7."));
                                    return;
                                }

                                if (subQuest.isCooldown(user)) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &7Musisz zaczekac &e" + subQuest.getRemainingCooldown(user) + " &7przed kolejnym oddaniem przedmiotu."));
                                    return;
                                }

                                Optional<ItemStack> questItem = Stream.of(player.getInventory().getContents())
                                        .filter(Objects::nonNull)
                                        .filter(ItemHelper::isNotAir)
                                        .filter(itemStack -> ItemHelper.isValidItem(itemStack, item))
                                        .findAny();

                                questItem.ifPresentOrElse(itemStack -> {
                                    if (itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
                                    else player.getInventory().remove(itemStack);
                                    user.getCooldownSubQuests().put(subQuest, Instant.now().plus(subQuest.getCooldown()));
                                    if (Math.random() * 100.0D <= subQuest.getChance(user)) {
                                        ref.ai.incrementAndGet();
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
                                        player.sendMessage(ChatHelper.format(" &8» &7Oddano &3przedmiot&7."));
                                        new QuestMenu(this.quests, subQuest, player, user).open(player);
                                    } else {
                                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                                        player.sendMessage(ChatHelper.format(" &8» &cNiestety nie udalo Ci sie oddac przedmiotu&7."));
                                    }
                                }, () -> {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &cNie masz zadnych przedmiotow do oddania&7."));
                                });

//                                int remainingAmount = subQuest.getAmount() - ai.get();
//                                int startAmount = remainingAmount;

//                                Set<ItemStack> items = Stream.of(player.getInventory().getContents())
//                                        .filter(Objects::nonNull)
//                                        .filter(ItemHelper::isNotAir)
//                                        .filter(itemStack -> ItemHelper.isValidItem(itemStack, item))
//                                        .collect(Collectors.toSet());

//                                for (ItemStack is : items) {
//                                    if (remainingAmount <= 0)
//                                        break;
//
//                                    if (remainingAmount == is.getAmount()) {
//                                        player.getInventory().remove(is);
//                                        ai.addAndGet(remainingAmount);
//                                        remainingAmount = 0;
//                                        break;
//                                    }
//
//                                    if (remainingAmount < is.getAmount()) {
//                                        ai.addAndGet(remainingAmount);
//                                        is.setAmount(is.getAmount() - remainingAmount);
//                                        remainingAmount = 0;
//                                        break;
//                                    }
//
//                                    if (remainingAmount > is.getAmount()) {
//                                        player.getInventory().remove(is);
//                                        ai.addAndGet(is.getAmount());
//                                        remainingAmount -= is.getAmount();
//                                    }
//                                }
//
//                                int itemsSold = startAmount - remainingAmount;
//
//                                if (itemsSold == 0) {
//                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
//                                    player.sendMessage(ChatHelper.format(" &8» &cNie masz zadnych przedmiotow do oddania&7."));
//                                } else {
//                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
//                                    player.sendMessage(ChatHelper.format(" &8» &7Oddano &3" + itemsSold + " przedmiotow&7."));
//                                }
//
//                                new QuestMenu(this.quests, subQuest, player, user).open(player);
//                                return;
                            }

                            // Logic for claiming the reward for quest.
                            if (event.getSlot() == 15) {
                                if (user.getCompletedSubQuests().contains(subQuest)) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &cJuz odebrales nagrode za tego questa&7."));
                                    player.closeInventory();
                                    return;
                                }

                                if (user.getProgress().getOrDefault(subQuest, new AtomicInteger()).get() >= subQuest.getAmount()) {
                                    subQuest.getRewards().forEach(reward -> this.quests.getServer().dispatchCommand(this.quests.getServer().getConsoleSender(), reward.replace("{PLAYER}", player.getName())));
                                    user.getProgress().remove(subQuest);
                                    user.getCompletedSubQuests().add(subQuest);
                                    user.getActiveSubQuests().remove(subQuest.getQuest());
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
                                    player.sendMessage(ChatHelper.format(" &8» &aOdebrano nagrode&7."));
                                    player.closeInventory();
                                    return;
                                }

                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                                player.sendMessage(ChatHelper.format(" &8» &eJeszcze nie mozesz odebrac nagrody za tego questa. &7Sprawdz postep po lewej stronie&7."));
                            }
                        }));
    }
}
