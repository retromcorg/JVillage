package com.johnymuffin.jvillage.beta.config;

import com.avaje.ebeaninternal.server.jmx.MAdminAutofetch;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class JVillageLanguage extends Configuration {
    private HashMap<String, String> map;

    public JVillageLanguage(File file) {
        super(file);
        map = new HashMap<String, String>();
        loadDefaults();
        //TODO: Implement a proper development mode
//        loadFile();
    }

    private void loadDefaults() {
        //General Stuff
        map.put("no_permission", "&4Sorry, you don't have permission for this command.");
        map.put("unavailable_to_console", "&4Sorry, console can't run this command.");
        map.put("player_not_found_full", "&4Can't find a player called &9%username%");
        map.put("generic_error", "&4Sorry, an error occurred running that command, please contact staff!");
        map.put("generic_error_player", "&4Sorry, an error occurred:&f %var1%");
        map.put("generic_no_save_data", "&4Sorry, JPerms has no information on that player.");
        map.put("generic_invalid_world", "&cSorry, a world with that name couldn't be located");
        map.put("generic_action_completed", "&bYour action has been completed");
        map.put("generic_not_implemented", "&cSorry, that feature is not yet implemented");
        map.put("village_not_found", "&4Sorry, a village with that name couldn't be located");
        map.put("no_village_selected", "&4Sorry, you don't have a village selected");
        map.put("not_in_village", "&4Sorry, you are not in that village");
        map.put("village_owner_leave", "&4Sorry, the owner of a village can't leave it");
        map.put("movement_village_enter", "&bYou have entered the village of &9%village%");
        map.put("movement_wilderness_enter", "&bYou have entered the wilderness");
        map.put("unknown_economy_error", "&4Sorry, an unknown economy error occurred");


        map.put("movement_autoselect_enter", "&bYour selected village has been set to &9%village% &bbecause you have entered it");

        map.put("build_denied", "&4Sorry, you don't have permission to build in &9%village%");
        map.put("destroy_denied", "&4Sorry, you don't have permission to destroy in &9%village%");
        map.put("ignite_denied", "&4Sorry, you don't have permission to \"ignite\" in &9%village% :(");

        //JVillage Admin command
        map.put("command_villageadmin_general_use", "&cSorry, that is invalid. Try /villageadmin (plugin|world|village|player)");
        map.put("command_villageadmin_plugin_use", "&cSorry, that is invalid. Try /villageadmin plugin (reload|save|version|import)");
        map.put("command_villageadmin_plugin_import_use", "&cSorry, that is invalid. Try /villageadmin plugin import (towny|factions)");
        map.put("command_villageadmin_world_use", "&cSorry, that is invalid. Try /villageadmin world (wgcleanup)");

        map.put("command_villageadmin_plugin_import_towny_start", "&bImporting Towny data. The server might freeze while this is happening.");
        map.put("command_villageadmin_plugin_import_towny_success", "&bImporting Towny data completed successfully. The debug is available in the console.");
        map.put("command_villageadmin_plugin_import_towny_fail", "&bImporting Towny data failed. The debug is available in the console.");

        map.put("command_villageadmin_village_use", "&cSorry, that is invalid. Try /villageadmin village (add|kick|setowner|delete|unclaim)");

        map.put("command_villageadmin_village_add_already_member", "&4Sorry, that player is already a member of that village");
        map.put("command_villageadmin_village_add_success", "&b%username% has been added to the village %village%");
        map.put("command_villageadmin_village_add_message", "&bYou have been added to the village %village% by %admin%");
        map.put("command_villageadmin_village_add_use", "&cSorry, that is invalid. Try /villageadmin village add <village> <player>");

        map.put("command_villageadmin_village_setowner_use", "&cSorry, that is invalid. Try /villageadmin village setowner <village> <player>");
        map.put("command_villageadmin_village_setowner_already_owner", "&4Sorry, that player is already the owner of that village");
        map.put("command_villageadmin_village_setowner_message", "&b%admin% has set %player% as the new owner of %village%");
        map.put("command_villageadmin_village_setowner_success", "&b%player% is now the owner of %village%");

        map.put("command_villageadmin_village_kick_use", "&cSorry, that is invalid. Try /villageadmin village kick <village> <player>");
        map.put("command_villageadmin_village_kick_is_owner", "&4Sorry, you can't kick the owner of a village. Please set someone else as the owner first.");
        map.put("command_villageadmin_village_kick_not_member", "&4Sorry, that player is not a member of that village");
        map.put("command_villageadmin_village_kick_success", "&b%player% has been kicked from %village%");

        map.put("command_villageadmin_village_delete_use", "&cSorry, that is invalid. Try /villageadmin village delete <village>");
        map.put("command_villageadmin_village_delete_broadcast", "&b%village% has been obliterated by %admin% using the power of the gods");
        map.put("command_villageadmin_village_delete_success", "&b%village% has been deleted successfully");

        map.put("command_villageadmin_village_unclaim_use", "&cSorry, that is invalid. Try /villageadmin village unclaim"); //Not needed
        map.put("command_villageadmin_village_unclaim_occurrence", "&bA claim has been removed for %village%");
        map.put("command_villageadmin_village_unclaim_success", "&bRemoved all claims for the chunk you are standing in.");

        map.put("command_villageadmin_world_wgcleanup_success", "&bWorldGuard cleanup completed successfully");

        //JVillage player command

        //JVillage player help commands
        map.put("command_village_use", "&9--- &bJVillage Menu&9---" +
                "\n&7Village In: %villagein%" +
                "\n&7Village Selected: %village%" +
                "\n&7Village Info: /v info [village]" +
                "\n&7Village Help: /v help");

        map.put("command_village_help_use", "&bPlease use &9/village help [player|assistant|owner|flags]");

        map.put("command_village_player_help", "&cJVillage Player Commands" +
                "\n&8- &7/village info [village] &8- &7Show Village Info" +
                "\n&8- &7/village help [&fplayer&7|assistant|owner]&8- &7Shows selected help page" +
                "\n&8- &7/village select [village] &8- &7Select a village to modify" +
                "\n&8- &7/village join [village] &8- &7Joins a village" +
                "\n&8- &7/village leave [village]&8- &7Leaves a village" +
                "\n&8- &7/village autoswitch [on/off] &8- &7Autoswitch town" +
                "\n&8- &7/village spawn &8- &7Teleport to village spawn");

        map.put("command_village_assistant_help", "&cJVillage Assistant Commands" +
                "\n&8- &7/village invite [name] &8- &7Invite a player to your selected town" +
                "\n&8- &7/village kick [name] &8- &7Kick a player from your selected town" +
                "\n&8- &7/village claim &8- &7Claim the chunk you are standing in" +
                "\n&8- &7/village unclaim &8- &7Unclaim the chunk you are standing in");

        map.put("command_village_owner_help", "&cJVillage Owner Commands" +
                "\n&8- &7/village create [name] &8- &7Create a new village" +
                "\n&8- &7/village setowner [name] &8- &7Promote to owner" +
                "\n&8- &7/village promote [name] &8- &7Promote a player to assistant" +
                "\n&8- &7/village demote [name] &8- &7Demote a player from assistant" +
                "\n&8- &7/village setspawn &8- &7Set the spawn point for your village" +
                "\n&8- &7/village rename [name] &8- &7Rename your village");

        map.put("command_village_info_use", "&6Information for %village%" +
                "\n&7Owner: %owner%" +
                "\n&7Assistants: %assistants%" +
                "\n&7Members: %members%" +
                "\n&7Claims: %claims%" +
                "\n&7Spawn: %spawn%");

        map.put("command_village_select_use", "&bYour selected village is &9%village%");
        map.put("command_village_select_none", "&cYou have no selected village");
        map.put("command_village_select_village", "&bYou have selected the village &9%village%");

        map.put("command_village_autoswitch_on", "&bYou have enabled auto switching");
        map.put("command_village_autoswitch_off", "&bYou have disabled auto switching");
        map.put("command_village_autoswitch_use", "&cSorry, that is invalid. Try /village autoswitch [on|off]");
        map.put("command_village_autoswitch_set", "&bYour auto switching has been set to &9%state%");

        map.put("command_village_leave_success", "&bYou have left the village &9%village%");
        map.put("command_village_leave_use", "&cSorry, that is invalid. Try /village leave [village]");
        map.put("command_village_leave_broadcast", "&f%player% has left the village.");

        map.put("command_village_invite_use", "&cSorry, that is invalid. Try /village invite [name]" +
                "\nThe player will be invited to your currently selected village");
        map.put("command_village_invite_already", "&cSorry, that player is already a member of %village%");
        map.put("command_village_invite_denied", "&cSorry, you don't have permission to invite players to %village%");
        map.put("command_village_invite_received", "&bYou have been invited to join &9%village%" +
                "\n&7Type &9/village join %village% &7to join the village");
        map.put("command_village_invite_sent", "&bYou have invited &9%player% &bto join &9%village%");
        map.put("command_village_invite_broadcast", "&f%player% has been invited by %villagemember%");

        map.put("command_village_join_use", "&cSorry, that is invalid. Try /village join [village]");
        map.put("command_village_join_success", "&bYou have joined the village &9%village%");
        map.put("command_village_join_denied", "&cSorry, you haven't received an invite to join %village%");
        map.put("command_village_join_limit", "&cSorry, you can't join %village%. You have reached the limit of %limit% villages");
        map.put("command_village_join_broadcast", "&f%player% has joined the village.");

        map.put("command_village_delete_use", "&cSorry, that is invalid. Try /village delete [village]");
        map.put("command_village_delete_not_owner", "&cSorry, you are not the owner so you can't delete %village%");
        map.put("command_village_delete_success", "&bYou have deleted the village &9%village%");

        map.put("command_village_delete_broadcast", "&bThe village &9%village% &bhas failed to maintain pace with the world and has fallen into ruin");

        map.put("command_village_create_use", "&cSorry, that is invalid. Try /village create [name]");
        map.put("command_village_create_invalid_name", "&cSorry, that is an invalid name. Please only use letters and numbers and less then 16 characters");
        map.put("command_village_create_already_exists", "&cSorry, that village name already exists");
        map.put("command_village_create_already_claimed", "&cSorry, that chunk is already claimed");
        map.put("command_village_create_success", "&bYou have created the village &9%village%");
        map.put("command_village_create_limit", "&cSorry, you can't create a village. You have reached the limit of %limit% villages");
        map.put("command_village_create_insufficient_funds", "&cSorry, you don't have enough money to create a village. It costs $%cost%");
        map.put("command_village_create_payment", "&bYou have paid &9$%amount% &bto create the village &9%village%");
        map.put("command_village_create_message", "&b%player% &bhas created the village &9%village%");

        map.put("command_village_claim_not_assistant", "&cSorry, you are not an assistant or owner of &9%village%&c so you can't claim chunks");
        map.put("command_village_claim_success", "&bYou have claimed the chunk you are standing in for &9%village%. &bIt cost &9$%cost%");
        map.put("command_village_claim_already_claimed", "&cSorry, that chunk is already claimed");
        map.put("command_village_claim_not_neighboring", "&cSorry, you can only claim chunks that are next to your village" +
                "\n&cIf you want to make an outpost, use /village claim outpost");
        map.put("command_village_claim_insufficient_funds", "&cSorry, you don't have enough money to claim this chunk. It costs $%cost%");
        map.put("command_village_claim_worldguard_denied", "&cSorry, you can't claim this chunk because it is protected by WorldGuard");

        map.put("command_village_unclaim_not_claimed", "&cSorry, that chunk is not claimed by &9%village%");
        map.put("command_village_unclaim_success", "&bYou have unclaimed the chunk you are standing in for &9%village%");
        map.put("command_village_unclaim_not_assistant", "&cSorry, you are not an assistant or owner of &9%village%&c so you can't unclaim chunks");

        map.put("command_village_kick_use", "&cSorry, that is invalid. Try /village kick [name]");
        map.put("command_village_kick_not_found", "&cSorry, the UUID of &9%player% &cwas not found");
        map.put("command_village_kick_not_member", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_kick_success", "&bYou have kicked &9%player% &bfrom &9%village%");
        map.put("command_village_kick_denied", "&cSorry, you are not the owner of &9%village%&c so you can't kick members");
        map.put("command_village_kick_message", "&bYou have been kicked from &9%village%");

        map.put("command_village_spawm_unsafe", "&cSorry, teleportation to %village% has been determined to be unsafe");
        map.put("command_village_spawn_success", "&bYou have been teleported to the spawn of &9%village%");
        map.put("command_village_spawn_not_member", "&cSorry, you are not a member of &9%village%&c so you can't teleport to the spawn");

        map.put("command_village_setowner_use", "&cSorry, that is invalid. Try /village setowner [name]");
        map.put("command_village_setowner_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't change the owner");
        map.put("command_village_setowner_not_in_village", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_setowner_message", "&bYou are now the owner of &9%village%");
        map.put("command_village_setowner_success", "&bYou have changed the owner of &9%village% &bto &9%player%");

        map.put("command_village_rename_use", "&cSorry, that is invalid. Try /village rename [name]");
        map.put("command_village_rename_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't change the name");
        map.put("command_village_rename_invalid_name", "&cSorry, that is an invalid name. Please only use letters and numbers and less then 22 characters");
        map.put("command_village_rename_already_exists", "&cSorry, that village name already exists");
        map.put("command_village_rename_broadcast", "&bThe village &9%village% &bhas been renamed to &9%new_village%");
        map.put("command_village_rename_success", "&bYou have changed the name of &9%village%");

        map.put("command_village_promote_use", "&cSorry, that is invalid. Try /village promote [name]");
        map.put("command_village_promote_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't promote members");
        map.put("command_village_promote_not_found", "&cSorry, the UUID of &9%player% &cwas not found");
        map.put("command_village_promote_not_in_village", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_promote_already_assistant", "&cSorry, that player is already an assistant of &9%village%");
        map.put("command_village_promote_success", "&bYou have promoted &9%player% &bto assistant of &9%village%");
        map.put("command_village_promote_message", "&bYou have been promoted to assistant of &9%village%");

        map.put("command_village_demote_use", "&cSorry, that is invalid. Try /village demote [name]");
        map.put("command_village_demote_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't demote members");
        map.put("command_village_demote_not_found", "&cSorry, the UUID of &9%player% &cwas not found");
        map.put("command_village_demote_not_in_village", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_demote_not_assistant", "&cSorry, that player is not an assistant of &9%village% so they can't be demoted.");
        map.put("command_village_demote_message", "&bYou have been demoted from assistant of &9%village%");
        map.put("command_village_demote_success", "&bYou have demoted &9%player% &bfrom assistant of &9%village%");

        map.put("command_village_setspawn_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't change the spawn");
        map.put("command_village_setspawn_not_in_village", "&cSorry, that location is not within an area claimed by &9%village%");

        map.put("command_resident_info", "&7----- &dResident Menu &7-----" +
                "\n&6Username: %username%" +
                "\n&bOwner of: %owner%" +
                "\n&aAssistant of: %assistant%" +
                "\n&eMember of: %member%");


        //JPerms command
        map.put("jperms_main_general_use", "&cSorry, that is invalid. Try /jperms (user/group/plugin)");
        map.put("jperms_user_general_use", "&cSorry, that is invalid. Try /jperms user (username/uuid) (group/perm)");
        map.put("jperms_user_perm_use", "&cSorry, that is invalid. Try /jperms user (username/uuid) perm (add/list/remove)");
        map.put("jperms_user_perm_add_use", "&cSorry, that is invalid. Try /jperms user (username/uuid) perm add (perm)");
        map.put("jperms_user_group_general_use", "&cSorry, that is invalid. Try /jperms user (username/uuid) group (set)");
        map.put("jperms_group_general_use", "&cSorry, that is invalid. Try /jperms group (group) (list/perm/inheritance)");
        map.put("jperms_group_general_unknown", "&cSorry, that group is unknown");
        map.put("jperms_user_perm_remove_use", "&cSorry, that is invalid. Try /jperms user (username/uuid) perm remove (perm)");
        map.put("jperms_plugin_use", "&cSorry, that is invalid. Try /jperms plugin (reload/save)");

    }

    private void loadFile() {
        this.load();
        for (String key : map.keySet()) {
            if (this.getString(key) == null) {
                this.setProperty(key, map.get(key));
            } else {
                map.put(key, this.getString(key));
            }
        }
        this.save();
    }

    public String getMessage(String msg) {
        String loc = map.get(msg);
        if (loc != null) {
            return loc.replace("&", "\u00a7");
        }
        return msg;
    }


}
