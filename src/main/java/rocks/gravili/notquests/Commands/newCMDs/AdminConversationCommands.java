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

package rocks.gravili.notquests.Commands.newCMDs;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import rocks.gravili.notquests.Commands.newCMDs.arguments.ConversationSelector;
import rocks.gravili.notquests.Conversation.Conversation;
import rocks.gravili.notquests.Conversation.ConversationLine;
import rocks.gravili.notquests.Conversation.ConversationManager;
import rocks.gravili.notquests.NotQuests;

import java.util.ArrayList;
import java.util.List;

import static rocks.gravili.notquests.Commands.NotQuestColors.*;

public class AdminConversationCommands {
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final NotQuests main;
    private final PaperCommandManager<CommandSender> manager;
    private final Command.Builder<CommandSender> conversationBuilder;

    private final ConversationManager conversationManager;


    public AdminConversationCommands(final NotQuests main, PaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> conversationBuilder, final ConversationManager conversationManager) {
        this.main = main;
        this.manager = manager;
        this.conversationBuilder = conversationBuilder;

        this.conversationManager = conversationManager;


        manager.command(conversationBuilder.literal("test")
                .senderType(Player.class)
                .meta(CommandMeta.DESCRIPTION, "Starts a test conversation.")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());
                    final Player player = (Player) context.getSender();

                    audience.sendMessage(miniMessage.parse(
                            mainGradient + "Playing test conversation..."
                    ));
                    conversationManager.playConversation(player, conversationManager.createTestConversation());
                }));

        manager.command(conversationBuilder.literal("list")
                .meta(CommandMeta.DESCRIPTION, "Lists all conversations.")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());

                    audience.sendMessage(miniMessage.parse(
                            highlightGradient + "All conversations:"
                    ));
                    int counter = 1;
                    for (final Conversation conversation : conversationManager.getAllConversations()) {
                        audience.sendMessage(miniMessage.parse(
                                highlightGradient + counter + ". </gradient>" + mainGradient + conversation.getIdentifier()
                        ));

                        audience.sendMessage(miniMessage.parse(
                                unimportant + "--- Attached to NPC: " + unimportantClose + mainGradient + conversation.getNPCID()
                        ));

                        audience.sendMessage(miniMessage.parse(
                                unimportant + "--- Amount of starting conversation lines: " + unimportantClose + mainGradient + conversation.getStartingLines().size()
                        ));
                    }

                }));

        manager.command(conversationBuilder.literal("analyze")
                .argument(ConversationSelector.of("conversation", main), ArgumentDescription.of("Name of the Conversation."))

                .meta(CommandMeta.DESCRIPTION, "Analyze specific conversations.")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());


                    final Conversation foundConversation = context.get("conversation");

                    audience.sendMessage(miniMessage.parse(
                            highlightGradient + "Starting lines (max. 3 levels of next):"
                    ));

                    for (final ConversationLine conversationLine : foundConversation.getStartingLines()) {
                        audience.sendMessage(miniMessage.parse(
                                highlightGradient + conversationLine.getIdentifier() + ":"
                        ));
                        audience.sendMessage(miniMessage.parse(
                                unimportant + "  Speaker: " + unimportantClose + mainGradient + conversationLine.getSpeaker().getSpeakerName()
                        ));
                        audience.sendMessage(miniMessage.parse(
                                unimportant + "  Message: " + unimportantClose + mainGradient + conversationLine.getMessage()
                        ));

                        audience.sendMessage(miniMessage.parse(
                                unimportant + "  Next: "
                        ));

                        if (conversationLine.getNext().size() >= 1) {
                            for (final ConversationLine next : conversationLine.getNext()) {
                                audience.sendMessage(miniMessage.parse(
                                        unimportant + " └" + highlightGradient + next.getIdentifier() + ":"
                                ));
                                audience.sendMessage(miniMessage.parse(
                                        "  " + unimportant + "  Speaker: " + unimportantClose + mainGradient + next.getSpeaker().getSpeakerName()
                                ));
                                audience.sendMessage(miniMessage.parse(
                                        "  " + unimportant + "  Message: " + unimportantClose + mainGradient + next.getMessage()
                                ));

                                if (next.getNext().size() >= 1) {
                                    audience.sendMessage(miniMessage.parse(
                                            "  " + unimportant + "  Next: " + unimportantClose
                                    ));


                                    for (final ConversationLine nextnext : next.getNext()) {
                                        audience.sendMessage(miniMessage.parse(
                                                unimportant + "   └" + highlightGradient + nextnext.getIdentifier() + ":"
                                        ));
                                        audience.sendMessage(miniMessage.parse(
                                                "    " + unimportant + "  Speaker: " + unimportantClose + mainGradient + nextnext.getSpeaker().getSpeakerName()
                                        ));
                                        audience.sendMessage(miniMessage.parse(
                                                "    " + unimportant + "  Message: " + unimportantClose + mainGradient + nextnext.getMessage()
                                        ));

                                        if (nextnext.getNext().size() >= 1) {
                                            audience.sendMessage(miniMessage.parse(
                                                    "    " + unimportant + "  Next: " + unimportantClose
                                            ));
                                            for (final ConversationLine nextnextnext : nextnext.getNext()) {
                                                audience.sendMessage(miniMessage.parse(
                                                        unimportant + "     └" + highlightGradient + nextnextnext.getIdentifier() + ":"
                                                ));
                                                audience.sendMessage(miniMessage.parse(
                                                        "      " + unimportant + "  Speaker: " + unimportantClose + mainGradient + nextnextnext.getSpeaker().getSpeakerName()
                                                ));
                                                audience.sendMessage(miniMessage.parse(
                                                        "      " + unimportant + "  Message: " + unimportantClose + mainGradient + nextnextnext.getMessage()
                                                ));
                                            }

                                        }

                                    }
                                }


                            }
                        }



                    }

                }));

        manager.command(conversationBuilder.literal("start")
                .argument(ConversationSelector.of("conversation", main), ArgumentDescription.of("Name of the Conversation."))
                .senderType(Player.class)
                .meta(CommandMeta.DESCRIPTION, "Starts a conversation.")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());
                    final Player player = (Player) context.getSender();

                    final Conversation foundConversation = context.get("conversation");


                    audience.sendMessage(miniMessage.parse(
                            mainGradient + "Playing " + foundConversation.getIdentifier() + " conversation..."
                    ));
                    conversationManager.playConversation(player, foundConversation);
                }));


        manager.command(conversationBuilder.literal("edit")
                .argument(ConversationSelector.of("conversation", main), ArgumentDescription.of("Name of the Conversation."))
                .literal("npc")
                .argument(IntegerArgument.<CommandSender>newBuilder("NPC").withSuggestionsProvider((context, lastString) -> {
                    ArrayList<String> completions = new ArrayList<>();
                    completions.add("-1");
                    for (final NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                        completions.add("" + npc.getId());
                    }
                    final List<String> allArgs = context.getRawInput();
                    final Audience audience = main.adventure().sender(context.getSender());
                    main.getUtilManager().sendFancyCommandCompletion(audience, allArgs.toArray(new String[0]), "[NPC ID]", "");

                    return completions;
                }).build(), ArgumentDescription.of("ID of the Citizens NPC which should start the conversation (set to -1 to disable)"))
                .meta(CommandMeta.DESCRIPTION, "Set conversation NPC (-1 = disabled)")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());

                    final Conversation foundConversation = context.get("conversation");
                    final int npcID = context.get("NPC");

                    foundConversation.setNPC(npcID);

                    audience.sendMessage(miniMessage.parse(
                            mainGradient + "NPC of conversation " + highlightGradient + foundConversation.getIdentifier() + "</gradient> has been set to "
                                    + highlight2Gradient + npcID + "</gradient>!"
                    ));
                }));


        manager.command(conversationBuilder.literal("edit")
                .argument(ConversationSelector.of("conversation", main), ArgumentDescription.of("Name of the Conversation."))
                .literal("armorstand")
                .literal("add", "set")
                .senderType(Player.class)
                .meta(CommandMeta.DESCRIPTION, "Gives you an item to add conversation to an armorstand")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());
                    final Player player = (Player) context.getSender();

                    final Conversation foundConversation = context.get("conversation");


                    ItemStack itemStack = new ItemStack(Material.PAPER, 1);
                    //give a specialitem. clicking an armorstand with that special item will remove the pdb.

                    NamespacedKey key = new NamespacedKey(main, "notquests-item");
                    NamespacedKey conversationIdentifierKey = new NamespacedKey(main, "notquests-conversation");

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    //Only paper List<Component> lore = new ArrayList<>();
                    List<String> lore = new ArrayList<>();

                    assert itemMeta != null;

                    itemMeta.getPersistentDataContainer().set(conversationIdentifierKey, PersistentDataType.STRING, foundConversation.getIdentifier());
                    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 8);


                    //Only paper itemMeta.displayName(Component.text("§dCheck Armor Stand", NamedTextColor.LIGHT_PURPLE));
                    itemMeta.setDisplayName("§dAdd conversation §b" + foundConversation.getIdentifier() + " §dto this Armor Stand");
                    //Only paper lore.add(Component.text("§fRight-click an Armor Stand to see which Quests are attached to it."));
                    lore.add("§fRight-click an Armor Stand to add the conversation §b" + foundConversation.getIdentifier() + " §fto it.");

                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    //Only paper itemMeta.lore(lore);

                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);

                    player.getInventory().addItem(itemStack);

                    audience.sendMessage(miniMessage.parse(
                            successGradient + "You have been given an item with which you can add the conversation " + highlightGradient + foundConversation.getIdentifier() + "</gradient> to an armor stand. Check your inventory!"
                    ));
                }));

        manager.command(conversationBuilder.literal("edit")
                .literal("armorstand")
                .literal("remove", "delete")
                .senderType(Player.class)
                .meta(CommandMeta.DESCRIPTION, "Gives you an item to remove all conversations from an armorstand")
                .handler((context) -> {
                    final Audience audience = main.adventure().sender(context.getSender());
                    final Player player = (Player) context.getSender();


                    ItemStack itemStack = new ItemStack(Material.PAPER, 1);
                    //give a specialitem. clicking an armorstand with that special item will remove the pdb.

                    NamespacedKey key = new NamespacedKey(main, "notquests-item");
                    NamespacedKey conversationIdentifierKey = new NamespacedKey(main, "notquests-conversation");

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    //Only paper List<Component> lore = new ArrayList<>();
                    List<String> lore = new ArrayList<>();

                    assert itemMeta != null;

                    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 9);


                    //Only paper itemMeta.displayName(Component.text("§dCheck Armor Stand", NamedTextColor.LIGHT_PURPLE));
                    itemMeta.setDisplayName("§dRemove all conversations from this Armor Stand");
                    //Only paper lore.add(Component.text("§fRight-click an Armor Stand to see which Quests are attached to it."));
                    lore.add("§fRight-click an Armor Stand to remove all conversations attached to it.");

                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    //Only paper itemMeta.lore(lore);

                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);

                    player.getInventory().addItem(itemStack);

                    audience.sendMessage(miniMessage.parse(
                            successGradient + "You have been given an item with which you remove all conversations from an armor stand. Check your inventory!"
                    ));
                }));


    }
}