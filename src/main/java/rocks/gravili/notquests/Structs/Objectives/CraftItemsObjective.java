/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.Structs.Objectives;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rocks.gravili.notquests.Commands.NotQuestColors;
import rocks.gravili.notquests.Commands.newCMDs.arguments.MaterialOrHandArgument;
import rocks.gravili.notquests.Commands.newCMDs.arguments.wrappers.MaterialOrHand;
import rocks.gravili.notquests.NotQuests;
import rocks.gravili.notquests.Structs.ActiveObjective;
import rocks.gravili.notquests.Structs.Quest;

public class CraftItemsObjective extends Objective {

    private final NotQuests main;
    private final ItemStack itemToCraft;

    public CraftItemsObjective(NotQuests main, final Quest quest, final int objectiveID, ItemStack itemToCraft, int amountToCraft) {
        super(main, quest, objectiveID, amountToCraft);
        this.main = main;
        this.itemToCraft = itemToCraft;
    }

    public CraftItemsObjective(NotQuests main, Quest quest, int objectiveNumber, int progressNeeded) {
        super(main, quest, objectiveNumber, progressNeeded);
        final String questName = quest.getQuestName();

        this.main = main;
        itemToCraft = main.getDataManager().getQuestsConfig().getItemStack("quests." + questName + ".objectives." + objectiveNumber + ".specifics.itemToCraft.itemstack");

    }

    public static void handleCommands(NotQuests main, PaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> addObjectiveBuilder) {
        manager.command(addObjectiveBuilder.literal("CraftItems")
                .argument(MaterialOrHandArgument.of("material", main), ArgumentDescription.of("Material of the item which needs to be crafted."))
                .argument(IntegerArgument.<CommandSender>newBuilder("amount").withMin(1), ArgumentDescription.of("Amount of items which need to be crafted."))
                .meta(CommandMeta.DESCRIPTION, "Adds a new CraftItems Objective to a quest.")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());
                    final Quest quest = context.get("quest");
                    final int amount = context.get("amount");

                    final MaterialOrHand materialOrHand = context.get("material");
                    ItemStack itemStack;
                    if (materialOrHand.hand) { //"hand"
                        if (context.getSender() instanceof Player player) {
                            itemStack = player.getInventory().getItemInMainHand();
                        } else {
                            audience.sendMessage(MiniMessage.miniMessage().parse(
                                    NotQuestColors.errorGradient + "This must be run by a player."
                            ));
                            return;
                        }
                    } else {
                        itemStack = new ItemStack(materialOrHand.material, 1);
                    }

                    CraftItemsObjective craftItemsObjective = new CraftItemsObjective(main, quest, quest.getObjectives().size() + 1, itemStack, amount);
                    quest.addObjective(craftItemsObjective, true);

                    audience.sendMessage(MiniMessage.miniMessage().parse(
                            NotQuestColors.successGradient + "CraftItems Objective successfully added to Quest " + NotQuestColors.highlightGradient
                                    + quest.getQuestName() + "</gradient>!</gradient>"
                    ));

                }));
    }

    @Override
    public void save() {
        main.getDataManager().getQuestsConfig().set("quests." + getQuest().getQuestName() + ".objectives." + getObjectiveID() + ".specifics.itemToCraft.itemstack", getItemToCraft());
    }

    @Override
    public void onObjectiveUnlock(final ActiveObjective activeObjective) {

    }

    public final ItemStack getItemToCraft() {
        return itemToCraft;
    }

    public final long getAmountToCraft() {
        return super.getProgressNeeded();
    }

    @Override
    public String getObjectiveTaskDescription(final String eventualColor, final Player player) {
        final String displayName;
        if (getItemToCraft().getItemMeta() != null) {
            displayName = getItemToCraft().getItemMeta().getDisplayName();
        } else {
            displayName = getItemToCraft().getType().name();
        }

        if (!displayName.isBlank()) {
            return main.getLanguageManager().getString("chat.objectives.taskDescription.craftItems.base", player)
                    .replace("%EVENTUALCOLOR%", eventualColor)
                    .replace("%ITEMTOCRAFTTYPE%", "" + getItemToCraft().getType())
                    .replace("%ITEMTOCRAFTNAME%", "" + displayName)
                    .replace("%(%", "(")
                    .replace("%)%", "§f)");
        } else {
            return main.getLanguageManager().getString("chat.objectives.taskDescription.craftItems.base", player)
                    .replace("%EVENTUALCOLOR%", eventualColor)
                    .replace("%ITEMTOCRAFTTYPE%", "" + getItemToCraft().getType())
                    .replace("%ITEMTOCRAFTNAME%", "")
                    .replace("%(%", "")
                    .replace("%)%", "");
        }


    }
}
