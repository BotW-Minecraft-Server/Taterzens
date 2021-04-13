package org.samo_lego.taterzens.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.samo_lego.taterzens.Taterzens.getLogger;

/**
 * List of permissions. Not meant to be changed, just to keep them in one place.
 */
public class PermissionList {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();
    public final String _comment1 = "// All permissions for Taterzens mod commands.";
    public final String _comment2 = "// Will be active only if LuckPerms is installed.";
    public final String _comment3 = "// These values are not editable, they're here to inform you.";

    public final String _comment_modPermissions = "/* Permissions for managing the mod itself. */";
    public final String _comment_taterzensWiki = "// Gets the link for the wiki.";
    public final String taterzens_wiki = "taterzens.wiki_info";
    public final String _comment_taterzensConfigReload = "// Enables reloading the config file.";
    public final String taterzens_config_reload = "taterzens.config.reload";

    public final String _comment_npcPermissions = "/* Permissions for managing Taterzens. */";
    public final String _comment_npcCreate = "// Handles Taterzen creation.";
    public final String npc_create = "taterzens.npc.create";

    public final String _comment_select = "/* Selecting Taterzens */";
    public final String _comment_npcSelect = "// Selects Taterzen (in front of the executor).";
    public final String npc_select = "taterzens.npc.select";
    public final String _comment_npcSelectId = "// Selects Taterzens by ID.";
    public final String npc_select_id = "taterzens.npc.select.id";

    public final String _comment_npcDeselect = "// Deselects Taterzen.";
    public final String npc_deselect = "taterzens.npc.select.deselect";

    public final String _comment_npcList = "// Lists Taterzens.";
    public final String npc_list = "taterzens.npc.list";

    public final String _comment_npcRemove = "// Remove Taterzens.";
    public final String npc_remove = "taterzens.npc.remove";

    public final String _comment_npcPresetSave = "// Save Taterzen to a preset file.";
    public final String npc_preset_save = "taterzens.npc.preset.save";

    public final String _comment_npcPresetLoad = "// Load Taterzen from a preset file.";
    public final String npc_preset_load = "taterzens.npc.preset.load";

    public final String _comment_npcTp = "// Teleport Taterzen to a given location.";
    public final String npc_tp = "taterzens.npc.tp";

    public final String _comment_edit = "/* Editing Taterzens */";
    public final String _comment_npcEditName = "// Edit Taterzen's name.";
    public final String npc_edit_name = "taterzens.npc.edit.name";

    public final String _comment_commands = "/* Editing Taterzen's commands */";
    public final String _comment_npcEditCommandsSetPermissionLevel = "// Manages permission level of Taterzen.";
    public final String npc_edit_commands_setPermissionLevel = "taterzens.npc.edit.commands.set_permission_level";
    public final String _comment_npcEditCommandsRemove = "// Removes command by ID from Taterzen.";
    public final String npc_edit_commands_remove = "taterzens.npc.edit.commands.remove";
    public final String _comment_npcEditCommandsAdd = "// Add command to Taterzen.";
    public final String npc_edit_commands_add = "taterzens.npc.edit.commands.add";
    public final String _comment_npcEditCommandsClear = "// Clears all commands of Taterzen.";
    public final String npc_edit_commands_clear = "taterzens.npc.edit.commands.clear";
    public final String _comment_npcEditCommandsList = "// List commands Taterzen.";
    public final String npc_edit_commands_list = "taterzens.npc.edit.commands.clear";

    public final String _comment_npcEditEntityType = "// Changes Taterzen's type to other entity type";
    public final String npc_edit_entityType = "taterzens.npc.edit.entity_type";

    public final String _comment_npcEditPath = "// Enters path edit mode for Taterzen.";
    public final String npc_edit_path = "taterzens.npc.edit.path";
    public final String _comment_npcEditPathClear = "// Clears Taterzen's path.";
    public final String npc_edit_path_clear = "taterzens.npc.edit.path.clear";

    public final String _comment_messages = "/* Editing Taterzen's messages */";
    public final String _comment_npcEditMessages = "// Enters message adding mode of Taterzen.";
    public final String npc_edit_messages = "taterzens.npc.edit.messages";
    public final String _comment_npcEditMessagesClear = "// Clears all the messages of Taterzen.";
    public final String npc_edit_messages_clear = "taterzens.npc.edit.messages.clear";
    public final String _comment_npcEditMessagesList = "// Lists all the messages of Taterzen.";
    public final String npc_edit_messages_list = "taterzens.npc.edit.messages.list";
    public final String _comment_npcEditMessagesDelete = "// Deletes Taterzen's message by ID.";
    public final String npc_edit_messages_delete = "taterzens.npc.edit.messages.delete";
    public final String _comment_npcEditMessagesDelay = "// Edits delay of the message by ID.";
    public final String npc_edit_messages_delay = "taterzens.npc.edit.messages.delay";
    public final String _comment_npcEditMessagesEdit = "// Enables editing selected message.";
    public final String npc_edit_messages_edit = "taterzens.npc.edit.messages.edit";

    public final String _comment_npcEditSkin = "// Enables changing Taterzen's skin.";
    public final String npc_edit_skin = "taterzens.npc.edit.skin";
    public final String _comment_npcEditSkinLayers = "// Enables changing Taterzen's skin layers.";
    public String npc_edit_skin_layers = "taterzens.npc.edit.skin.layers";

    public final String _comment_npcEditEquipment = "// Enables changing Taterzen's equipment.";
    public final String npc_edit_equipment = "taterzens.npc.edit.equipment";
    public final String npc_edit_equipment_equipmentDrops = "taterzens.npc.edit.equipment.drops";

    public final String _comment_npcEditMovement = "// Enables editing Taterzen's movement.";
    public final String npc_edit_movement = "taterzens.npc.edit.movement";

    public final String _comment_npcEditBehaviour = "// Enables editing Taterzen's behaviour.";
    public final String npc_edit_behaviour = "taterzens.npc.edit.behaviour";

    public final String _comment_npcEditInvulnerability = "// Enables editing Taterzen's behaviour.";
    public final String npc_edit_tags_invulnerability = "taterzens.npc.edit.tags.invulnerability";

    public final String _comment_npcEditProfession = "// Allows setting a new profession to Taterzen.";
    public final String npc_edit_profession = "taterzens.npc.edit.profession";

    /**
     * Saves the permission list to the given file.
     *
     * @param file file to save permissions to
     */
    public void savePermissionList(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            getLogger().error("Problem occurred when saving permission list: " + e.getMessage());
        }
    }
}