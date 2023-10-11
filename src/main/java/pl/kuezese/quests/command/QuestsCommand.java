package pl.kuezese.quests.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.kuezese.quests.Quests;
import pl.kuezese.quests.helper.ChatHelper;
import pl.kuezese.quests.helper.NumberHelper;
import pl.kuezese.quests.object.Quest;

@RequiredArgsConstructor
public class QuestsCommand implements CommandExecutor {

    private final Quests quests;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatHelper.format("&aThis server is running " + this.quests.getDescription().getFullName() + " by " + this.quests.getDescription().getAuthors().get(0) + "."));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("quests.admin")) {
                    sender.sendMessage(ChatHelper.format("&cNie posiadasz dostepu do tej komendy. &8(&cquests.admin&8)"));
                    return true;
                }

                this.quests.getQuestManager().load(this.quests);
                sender.sendMessage(ChatHelper.format("&aPrzeladowano konfiguracje questow."));
                return true;
            }
            case "admin" -> {
                if (!sender.hasPermission("quests.admin")) {
                    sender.sendMessage(ChatHelper.format("&cNie posiadasz dostepu do tej komendy. &8(&cquests.admin&8)"));
                    return true;
                }

                if (args.length < 5) {
                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> <action> <player> <value>"));
                    return true;
                }

                Integer questId = NumberHelper.parseNumber(args[2]);

                if (questId == null) {
                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> <action> <player> <value>"));
                    return true;
                }

                Quest quest = Quest.getById(questId);

                if (quest == null) {
                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> <action> <player> <value>"));
                    return true;
                }

                String action = args[3];

                switch (action) {
                    case "cooldown" -> {
                        if (args.length < 6) {
                            sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> cooldown <action> <player> <value>"));
                            return true;
                        }

                        String cooldownAction = args[5];
                        switch (cooldownAction) {
                            case "reduce" -> {
                                if (args.length < 7) {
                                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> cooldown reduce <player> <value>"));
                                    return true;
                                }
                                String value = args[6];
                                // Implement cooldown reduction logic.
                            }
                            case "increase" -> {
                                if (args.length < 7) {
                                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> cooldown increase <player> <value>"));
                                    return true;
                                }
                                String value = args[6];
                                // Implement cooldown increase logic.
                            }
                            case "set" -> {
                                if (args.length < 7) {
                                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> cooldown set <player> <value>"));
                                    return true;
                                }
                                String value = args[6];
                                // Implement cooldown set logic.
                            }
                            case "reset" -> {

                            }
                            // Implement cooldown reset logic.
                            default -> sender.sendMessage("Invalid cooldown action.");
                        }
                    }
                    case "chance" -> {
                        if (args.length < 6) {
                            sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> chance <action> <player> <value>"));
                            return true;
                        }

                        Player player = this.quests.getServer().getPlayer(args[5]);

                        if (player == null) {
                            sender.sendMessage(ChatHelper.format("&cPodany gracz jest offline."));
                            return true;
                        }

                        String chanceAction = args[5];
                        switch (chanceAction) {
                            case "set" -> {
                                if (args.length < 7) {
                                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> chance set <player> <value>"));
                                    return true;
                                }
                                String value = args[6];
                                double chance = Double.parseDouble(value);
                                this.quests.getUserManager().find(player.getUniqueId(), user -> user.getChanceModifiers().put(quest, chance));
                                sender.sendMessage(ChatHelper.format("&7Ustawiles mnoznik szansy graczowi &a" + player.getName() + " &7na quescie o id &a" + quest.getId() + " &7na &ax" + chance + "&7."));
                            }
                            case "reset" -> {
                                this.quests.getUserManager().find(player.getUniqueId(), user -> user.getChanceModifiers().remove(quest));
                                sender.sendMessage(ChatHelper.format("&7Zresetowales mnoznik szansy graczowi &a" + player.getName() + " &7na quescie o id &a" + quest.getId() + "&7."));
                            }
                            // Implement chance reset logic.
                            default ->
                                    sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> chance <action> <player> <value>"));
                        }
                    }
                    default ->
                            sender.sendMessage(ChatHelper.format("&7Poprawne uzycie: &a/quests admin quest <quest_id> <action> <player> <value>"));
                }
                return true;
            }
            default -> {
                sender.sendMessage(ChatHelper.format("&aThis server is running " + this.quests.getDescription().getFullName() + " by " + this.quests.getDescription().getAuthors().get(0) + "."));
                return true;
            }
        }
    }
}
