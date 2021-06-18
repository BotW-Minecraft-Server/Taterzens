package org.samo_lego.taterzens.commands.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.samo_lego.taterzens.commands.NpcCommand;
import org.samo_lego.taterzens.interfaces.TaterzenEditor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.samo_lego.taterzens.Taterzens.PERMISSIONS;
import static org.samo_lego.taterzens.Taterzens.config;
import static org.samo_lego.taterzens.compatibility.LoaderSpecific.permissions$checkPermission;
import static org.samo_lego.taterzens.util.TextUtil.*;

public class MessagesCommand {

    public static void registerNode(LiteralCommandNode<ServerCommandSource> editNode) {
        LiteralCommandNode<ServerCommandSource> messagesNode = literal("messages")
                .then(literal("clear")
                        .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages_clear, config.perms.npcCommandPermissionLevel))
                        .executes(MessagesCommand::clearTaterzenMessages)
                )
                .then(literal("list")
                        .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages_list, config.perms.npcCommandPermissionLevel))
                        .executes(MessagesCommand::listTaterzenMessages)
                )
                .then(argument("message id", IntegerArgumentType.integer(0))
                        .then(literal("delete")
                                .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages_delete, config.perms.npcCommandPermissionLevel))
                                .executes(MessagesCommand::deleteTaterzenMessage)
                        )
                        .then(literal("setDelay")
                                .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages_delay, config.perms.npcCommandPermissionLevel))
                                .then(argument("delay", IntegerArgumentType.integer())
                                        .executes(MessagesCommand::editMessageDelay)
                                )
                        )
                        .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages, config.perms.npcCommandPermissionLevel))
                        .executes(MessagesCommand::editMessage)
                )
                .requires(src -> permissions$checkPermission(src, PERMISSIONS.npc_edit_messages_edit, config.perms.npcCommandPermissionLevel))
                .executes(MessagesCommand::editTaterzenMessages).build();
        
        editNode.addChild(messagesNode);
    }

    private static int deleteTaterzenMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        return NpcCommand.selectedTaterzenExecutor(source.getPlayer(), taterzen -> {
            int selected = IntegerArgumentType.getInteger(context, "message id") - 1;
            if(selected >= taterzen.getMessages().size()) {
                source.sendFeedback(
                        errorText("taterzens.command.message.error.404", String.valueOf(selected)),
                        false
                );
            } else {
                source.sendFeedback(successText("taterzens.command.message.deleted", taterzen.getMessages().get(selected).getFirst().getString()), false);
                taterzen.removeMessage(selected);
            }
        });
    }

    private static int editMessageDelay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        return NpcCommand.selectedTaterzenExecutor(source.getPlayer(), taterzen -> {
            int selected = IntegerArgumentType.getInteger(context, "message id") - 1;
            if(selected >= taterzen.getMessages().size()) {
                source.sendFeedback(
                        errorText("taterzens.command.message.error.404", String.valueOf(selected)),
                        false
                );
            } else {
                int delay = IntegerArgumentType.getInteger(context, "delay");
                taterzen.setMessageDelay(selected, delay);
                source.sendFeedback(successText("taterzens.command.message.delay", String.valueOf(delay)), false);
            }
        });
    }

    private static int editMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        return NpcCommand.selectedTaterzenExecutor(player, taterzen -> {
            ((TaterzenEditor) player).setEditorMode(TaterzenEditor.Types.MESSAGES);
            int selected = IntegerArgumentType.getInteger(context, "message id") - 1;
            if(selected >= taterzen.getMessages().size()) {
                source.sendFeedback(
                        successText("taterzens.command.message.list", String.valueOf(selected)),
                        false
                );
            } else {
                ((TaterzenEditor) player).setEditingMessageIndex(selected);
                source.sendFeedback(
                        successText("taterzens.command.message.editor.enter", taterzen.getMessages().get(selected).getFirst().getString()),
                        false)
                ;
            }
        });
    }

    private static int listTaterzenMessages(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        return NpcCommand.selectedTaterzenExecutor(source.getPlayer(), taterzen -> {
            ArrayList<Pair<Text, Integer>> messages = taterzen.getMessages();

            MutableText response = joinText("taterzens.command.message.list", Formatting.AQUA, Formatting.YELLOW, taterzen.getName().getString());
            AtomicInteger i = new AtomicInteger();

            messages.forEach(pair -> {
                int index = i.get() + 1;
                response.append(
                        new LiteralText("\n" + index + "-> ")
                                .formatted(index % 2 == 0 ? Formatting.YELLOW : Formatting.GOLD)
                                .append(pair.getFirst())
                                .styled(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc edit messages " + index))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("taterzens.tooltip.edit", index))
                                        ))
                )
                        .append("   ")
                        .append(
                                new LiteralText("X")
                                        .formatted(Formatting.RED)
                                        .formatted(Formatting.BOLD)
                                        .styled(style -> style
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("taterzens.tooltip.delete", index)))
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/npc edit messages " + index + " delete"))
                                        )
                        );
                i.incrementAndGet();
            });
            source.sendFeedback(response, false);
        });
    }

    private static int clearTaterzenMessages(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        return NpcCommand.selectedTaterzenExecutor(source.getPlayer(), taterzen -> {
            taterzen.clearMessages();
            source.sendFeedback(successText("taterzens.command.message.clear", taterzen.getName().getString()), false);
        });
    }

    private static int editTaterzenMessages(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        return NpcCommand.selectedTaterzenExecutor(player, taterzen -> {
            if(((TaterzenEditor) player).getEditorMode() == TaterzenEditor.Types.MESSAGES) {
                // Exiting the message edit mode
                ((TaterzenEditor) player).setEditorMode(TaterzenEditor.Types.NONE);
                ((TaterzenEditor) player).setEditingMessageIndex(-1);
                source.sendFeedback(
                        translate("taterzens.command.equipment.exit").formatted(Formatting.LIGHT_PURPLE),
                        false
                );
            } else {
                // Entering the edit mode
                ((TaterzenEditor) player).setEditorMode(TaterzenEditor.Types.MESSAGES);
                source.sendFeedback(
                        joinText("taterzens.command.message.editor.enter", Formatting.LIGHT_PURPLE, Formatting.AQUA, taterzen.getName().getString())
                                .formatted(Formatting.BOLD)
                                .styled(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc edit messages"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("taterzens.tooltip.exit").formatted(Formatting.RED)))
                                ),
                        false
                );
                source.sendFeedback(
                        successText("taterzens.command.message.editor.desc.1", taterzen.getName().getString())
                                .append("\n")
                                .append(translate("taterzens.command.message.editor.desc.2"))
                                .formatted(Formatting.GREEN),
                        false
                );
            }
        });
    }
}