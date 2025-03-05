package com.johnymuffin.jvillage.beta.config;

import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class JVillageLanguage extends Configuration {
    private HashMap<String, String> map;

    public JVillageLanguage(File file, boolean dev) {
        super(file);
        map = new HashMap<String, String>();
        loadDefaults();
        if(!dev) {
            loadFile();
        }
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
        map.put("no_village_selected_or_name_invalid", "&4Sorry, you don't have a village selected or the name you entered is invalid");
        map.put("command_invalid_argument_provide_integer", "&4Sorry, that is not a valid number. Please provide an integer");

        map.put("economy_disabled", "&4Sorry, the JVillage economy is disabled on this server");

        map.put("not_in_village", "&4Sorry, you are not in that village");
        map.put("village_owner_leave", "&4Sorry, the owner of a village can't leave it");
        map.put("movement_village_enter", "&bYou have entered the village of &9%village%");
        map.put("movement_wilderness_enter", "&bYou have entered the wilderness");
        map.put("unknown_economy_error", "&4Sorry, an unknown economy error occurred");

        map.put("assistant_or_higher", "&4Sorry, you must be an assistant or higher to do that");
        map.put("owner_or_higher", "&4Sorry, you must be the owner or higher to do that");


        map.put("movement_autoselect_enter", "&bYour selected village has been set to &9%village% &bbecause you have entered it");

        map.put("build_denied", "&4Sorry, you don't have permission to build in &9%village%");
        map.put("destroy_denied", "&4Sorry, you don't have permission to destroy in &9%village%");
        map.put("ignite_denied", "&4Sorry, you don't have permission to \"ignite\" in &9%village% :(");
        map.put("pvp_denied", "&4Sorry, PvP is disabled in &9%village%");

        //JVillage Admin command
        map.put("command_villageadmin_general_use", "&cSorry, that is invalid. Try /villageadmin (plugin|world|village|player)");
        map.put("command_villageadmin_plugin_use", "&cSorry, that is invalid. Try /villageadmin plugin (reload|save|version|import|debug)");
        map.put("command_villageadmin_plugin_import_use", "&cSorry, that is invalid. Try /villageadmin plugin import (towny|factions)");
        map.put("command_villageadmin_world_use", "&cSorry, that is invalid. Try /villageadmin world (wgcleanup)");

        map.put("command_villageadmin_plugin_import_towny_start", "&bImporting Towny data. The server might freeze while this is happening.");
        map.put("command_villageadmin_plugin_import_towny_success", "&bImporting Towny data completed successfully. The debug is available in the console.");
        map.put("command_villageadmin_plugin_import_towny_fail", "&bImporting Towny data failed. The debug is available in the console.");

        map.put("command_villageadmin_plugin_import_factions_start", "&bImporting Factions data. The server might freeze while this is happening.");
        map.put("command_villageadmin_plugin_import_factions_success", "&bImporting Factions data completed successfully. The debug is available in the console.");
        map.put("command_villageadmin_plugin_import_factions_fail", "&bImporting Factions data failed. The debug is available in the console.");

        map.put("command_villageadmin_plugin_debug_change", "&bDebug mode has been changed to &9%state%");

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
        map.put("command_villageadmin_village_unclaim_success", "&bUnclaimed the chunk you are standing in.");

        map.put("command_villageadmin_world_wgcleanup_success", "&bWorldGuard cleanup completed successfully");

        //JVillage player command

        //JVillage player help commands
        map.put("command_village_unknown", "&cSorry, that is invalid. Try /v and /v help");

        map.put("command_village_use", "&9--- &bJVillage Menu&9---" +
                "\n&7Village In: %villagein%" +
                "\n&7Village Selected: %village%" +
                "\n&7Village Info: /v info [village]" +
                "\n&7Village List: /v list" +
                "\n&7Village Help: /v help");

        map.put("command_village_help_use", "&bPlease use &9/village help [player|assistant|owner|flags]");

        map.put("command_village_player_help", "&cJVillage Player Commands" +
                "\n&8- &7/village info [village] &8- &7Show Village Info" +
                "\n&8- &7/village help [&fplayer&7|assistant|owner]&8- &7Shows selected help page" +
                "\n&8- &7/village select [village] &8- &7Select a village to modify" +
                "\n&8- &7/village join [village] &8- &7Joins a village" +
                "\n&8- &7/village leave [village]&8- &7Leaves a village" +
                "\n&8- &7/village autoswitch [on/off] &8- &7Autoswitch town" +
                "\n&8- &7/village balance [village] &8- &7Shows village balance" +
                "\n&8- &7/village deposit [village] [amount] &8- &7Deposit money into village bank" +
                "\n&8- &7/village warp [name] &8- &7Teleport to a village warp" +
                "\n&8- &7/village spawn &8- &7Teleport to village spawn");

        map.put("command_village_assistant_help", "&cJVillage Assistant Commands" +
                "\n&8- &7/village invite [name] &8- &7Invite a player to your selected town" +
                "\n&8- &7/village kick [name] &8- &7Kick a player from your selected town" +
                "\n&8- &7/village claim &8- &7Claim the chunk you are standing in" +
                "\n&8- &7/village claim rectangle [chunk radius] &8- &7Claim a rectangle of chunks" +
                "\n&8- &7/village claim auto &8- &7Claim chunks automatically as you walk (run again to disable)" +
                "\n&8- &7/village withdraw [village] [amount] &8- &7Withdraw money from village bank" +
                "\n&8- &7/village unclaim &8- &7Unclaim the chunk you are standing in" +
                "\n&8- &7/village unclaim auto &8- &7Unclaim chunks automatically as you walk" +
                "\n&8- &7/village setwarp [name] &8- &7Set a village warp" +
                "\n&8- &7/village delwarp [name] &8- &7Delete a village warp");

        map.put("command_village_owner_help", "&cJVillage Owner Commands" +
                "\n&8- &7/village create [name] &8- &7Create a new village" +
                "\n&8- &7/village setowner [name] &8- &7Promote to owner" +
                "\n&8- &7/village promote [name] &8- &7Promote a player to assistant" +
                "\n&8- &7/village demote [name] &8- &7Demote a player from assistant" +
                "\n&8- &7/village setspawn &8- &7Set the spawn point for your village" +
                "\n&8- &7/village rename [name] &8- &7Rename your village");

        map.put("command_village_flag_help", "&cJVillage Flag Commands" +
                "\n&8- &7/village flag [flag] [value] &8- &7Set a flag for your village" +
                "\n&8- &7/village flag list &8- &7List all flags for your village");

        map.put("command_village_flag_unknown", "&cSorry, that flag does not exist. Try /village flag list");
        map.put("command_village_flag_invalid_value", "&cSorry, that is not a valid value for that flag. Use true or false.");
        map.put("command_village_flag_set_success", "&bFlag %flag% has been set to %value%");

        map.put("command_village_info_use", "&6Information for %village%" +
                "\n&9Owner: &e%owner%" +
                "\n&9Balance: &a$%balance%" +
                "\n&9Assistants: &c%assistants%" +
                "\n&9Members: &b%members%" +
                "\n&9Claims: &d%claims%" +
                "\n&9Spawn: &f%spawn%");


        map.put("command_village_list_use", "&9--- &bJVillage List &9---" +
                "\n&6Village: &e%village%" +
                "\n&3Owner: &b%owner%" +
                "\n&5Assistants: &d%assistants%" +
                "\n&2Members: &a%members%");

        map.put("command_village_flag_list_use", "&9--- &bJVillage Flags&9---" +
                "%flags%");


        map.put("command_village_select_use", "&bYour selected village is &9%village%");
        map.put("command_village_select_none", "&cYou have no selected village");
        map.put("command_village_select_village", "&bYou have selected the village &9%village%");

        map.put("command_village_autoswitch_on", "&bYou have enabled auto switching");
        map.put("command_village_autoswitch_off", "&bYou have disabled auto switching");
        map.put("command_village_autoswitch_use", "&cSorry, that is invalid. Try /village autoswitch [on|off]");
        map.put("command_village_autoswitch_set", "&bYour auto switching is set to &9%state%");

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
        map.put("command_village_create_invalid_name", "&cSorry, that is an invalid name. Please only use letters and numbers and less than %max% characters");
        map.put("command_village_create_already_exists", "&cSorry, that village name already exists");
        map.put("command_village_create_already_claimed", "&cSorry, that chunk is already claimed");
        map.put("command_village_create_success", "&bYou have created the village &9%village%");
        map.put("command_village_create_limit", "&cSorry, you can't create a village. You have reached the limit of %limit% villages");
        map.put("command_village_create_insufficient_funds", "&cSorry, you don't have enough money to create a village. It costs $%cost%");
        map.put("command_village_create_payment", "&bYou have paid &9$%amount% &bto create the village &9%village%");
        map.put("command_village_create_message", "&b%player% &bhas created the village &9%village%");
        map.put("command_village_create_too_close", "&cSorry, you are too close to another village. You need to be at least %min% blocks away");


        map.put("command_village_claim_not_assistant", "&cSorry, you are not an assistant or owner of &9%village%&c so you can't claim chunks");
        map.put("command_village_claim_success", "&bYou have claimed the chunk you are standing in for &9%village%. &bIt cost &9$%cost%");
        map.put("command_village_claim_already_claimed", "&cSorry, that chunk is already claimed");
        map.put("command_village_claim_not_neighboring", "&cSorry, you can only claim chunks that are next to your village" +
                "\n&cIf you want to make an outpost, use /village claim outpost");
        map.put("command_village_claim_insufficient_funds", "&cInsufficient funds for $%cost% chunk.\n&cDeposit more with /village deposit.");
        map.put("command_village_claim_worldguard_denied", "&cSorry, you can't claim this chunk because it is protected by WorldGuard");
        //Area Claiming Messages
        map.put("command_village_claim_rectangle_not_enough_arguments", "&cSorry, that is invalid. Try /village claim rectangle [chunk radius]");
        //map.put("command_village_claim_circle_not_enough_arguments", "&cSorry, that is invalid. Try /village claim circle [chunk radius]");
        map.put("command_village_claim_rectangle_too_big", "&cSorry, the maximum rectangular chunk radius is %size%");
        map.put("command_village_claim_rectangle_other_already_claimed", "&cSorry, one of the chunks you are trying to claim is already claimed. %chunk% by %village%");
        map.put("command_village_claim_rectangle_insufficient_funds", "&cInsufficient funds for claiming %chunks% chunks. Cost: $%cost%");
        map.put("command_village_claim_rectangle_success", "&bYou have claimed %chunks% chunks for &9%village%. &bIt cost &9$%cost%");
        map.put("command_village_claim_rectangle_not_in_claim", "&cSorry, the chunk your standing in needs to be claimed by your village to use this command");
        map.put("command_village_claim_rectangle_already_claimed", "&cSorry, all the chunks you are trying to claim are already claimed by your village");
        map.put("command_village_claim_rectangle_broadcast", "&b%player% &bhas done a rectangle claim, claiming %chunks% chunks for a total of &9$%cost%");
        map.put("command_village_claim_autoclaim_on", "&bYou have turned on autoclaim. You will now automatically claim chunks as you walk around");
        map.put("command_village_claim_autoclaim_off", "&bYou have turned off autoclaim. You will no longer automatically claim chunks as you walk around");


        map.put("command_village_unclaim_not_claimed", "&cSorry, that chunk is not claimed by &9%village%");
        map.put("command_village_unclaim_success", "&bUnclaimed the chunk you are standing in. You have been refunded $%refund%");
        map.put("command_village_unclaim_not_assistant", "&cSorry, you are not an assistant or owner of &9%village%&c so you can't unclaim chunks");
        map.put("command_village_unclaim_spawn_block", "&cSorry, you can't unclaim the chunk that contains the village spawn");
        map.put("command_village_claim_autounclaim_on", "&bYou have turned on auto unclaim. You will now automatically unclaim chunks as you walk around");
        map.put("command_village_claim_autounclaim_off", "&bYou have turned off auto unclaim. You will no longer automatically unclaim chunks as you walk around");

        map.put("command_village_kick_use", "&cSorry, that is invalid. Try /village kick [name]");
        map.put("command_village_kick_not_found", "&cSorry, the UUID of &9%player% &cwas not found");
        map.put("command_village_kick_not_member", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_kick_success", "&bYou have kicked &9%player% &bfrom &9%village%");
        map.put("command_village_kick_denied", "&cSorry, you are not the owner of &9%village%&c so you can't kick members");
        map.put("command_village_kick_message", "&bYou have been kicked from &9%village%");

        map.put("command_village_spawn_unsafe", "&cSorry, teleportation to %village% has been determined to be unsafe");
        map.put("command_village_spawn_success", "&bYou have been teleported to the spawn of &9%village%");
        map.put("command_village_spawn_not_member", "&cSorry, you are not a member of &9%village%&c so you can't teleport to the spawn");

        map.put("command_village_setowner_use", "&cSorry, that is invalid. Try /village setowner [name]");
        map.put("command_village_setowner_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't change the owner");
        map.put("command_village_setowner_not_in_village", "&cSorry, that player is not a member of &9%village%");
        map.put("command_village_setowner_message", "&bYou are now the owner of &9%village%");
        map.put("command_village_setowner_success", "&bYou have changed the owner of &9%village% &bto &9%player%");

        map.put("command_village_rename_use", "&cSorry, that is invalid. Try /village rename [name]");
        map.put("command_village_rename_not_owner", "&cSorry, you are not the owner of &9%village%&c so you can't change the name");
        map.put("command_village_rename_invalid_name", "&cSorry, that is an invalid name. Please only use letters and numbers and less than %max% characters");
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
        map.put("command_village_setspawn_set_broadcast", "&9%player% &bhas set the spawn point to &9%cords%");

        map.put("command_village_setwarp_use", "&cSorry, that is invalid. Try /village setwarp [name]");
        map.put("command_village_setwarp_no_permission", "&cSorry, you don't have permission to set warps in &9%village%");
        map.put("command_village_setwarp_not_in_village", "&cSorry, that location is not within an area claimed by &9%village%");
        map.put("command_village_setwarp_invalid_name", "&cSorry, that is an invalid name. Please only use letters and numbers and less than %max% characters");
        map.put("command_village_setwarp_already_exists", "&cSorry, that warp name already exists");
        map.put("command_village_setwarp_insufficient_funds", "&cSorry, you don't have enough money to set a warp. It costs $%cost%");
        map.put("command_village_setwarp_payment", "&bYou have paid &9$%amount% &bto set the warp &9%warp%");
        map.put("command_village_setwarp_set_broadcast", "&9%player% &bhas set the warp &9%warp%");


        map.put("command_village_delwarp_use", "&cSorry, that is invalid. Try /village delwarp [name]");
        map.put("command_village_delwarp_no_permission", "&cSorry, you don't have permission to delete warps in &9%village%");
        map.put("command_village_delwarp_not_found", "&cSorry, the warp &9%warp% &cdoes not exist in &9%village%");
        map.put("command_village_delwarp_del_broadcast", "&9%player% &bhas deleted the warp &9%warp%");
        
        map.put("command_village_warp_list", "&bWarp list of &9%village%:");
        map.put("command_village_warp_success", "&bYou have been teleported to &9%warp%");
        map.put("command_village_warp_unsafe", "&cSorry, teleportation to %warp% has been determined to be unsafe");
        map.put("command_village_warp_not_found", "&cSorry, the warp &9%warp% &cdoes not exist in &9%village%");

        //Village Economy

        map.put("command_village_deposit_use", "&cSorry, that is invalid. Try /village deposit <village> [amount]");
        map.put("command_village_deposit_invalid_amount", "&cSorry, that is an invalid amount. Please only use numbers");
        map.put("command_village_deposit_success", "&bYou have deposited &9%amount% &binto the bank of &9%village%");
        map.put("command_village_deposit_broadcast", "&9%player% &bhas deposited &9$%amount% &binto the bank.");
        map.put("command_village_deposit_no_funds", "&cSorry, you don't have enough money to deposit that much");
        map.put("command_village_deposit_not_member", "&cSorry, you are not a member of &9%village%&c so you can't deposit money");

        map.put("command_village_withdraw_use", "&cSorry, that is invalid. Try /village withdraw [village] [amount]");
        map.put("command_village_withdraw_success", "&bYou have withdrawn &9%amount% &bfrom the bank of &9%village%");
        map.put("command_village_withdraw_broadcast", "&9%player% &bhas withdrawn &9$%amount% &bfrom the bank.");
        map.put("command_village_withdraw_no_funds", "&cSorry, the village doesn't have enough money to withdraw that much");
        map.put("command_village_withdraw_no_permission", "&cSorry, you don't have permission to withdraw money from &9%village%");

        map.put("command_village_balance_use", "&cSorry, that is invalid. Try /village balance [village]");
        map.put("command_village_balance_message", "&bThe village &9%village% &bhas &9$%balance% &bin the bank");


        map.put("command_resident_info", "&7----- &dResident Menu &7-----" +
                "\n&6Username: %username%" +
                "\n&bOwner of: %owner%" +
                "\n&aAssistant of: %assistant%" +
                "\n&eMember of: %member%");


        map.put("autoclaim_selected_village_disabled", "&cSorry, autoclaim has been disabled as your selected village has changed");
        map.put("autoclaim_disabled", "&cAutoclaim has been disabled");
        map.put("autounclaim_disabled", "&cAuto unclaim has been disabled");
        map.put("autoclaim_enter_worldguard_disabled", "&cSorry, autoclaim has been disabled as you have entered a protected World Guard region");
        map.put("autoclaim_not_neighbouring_disabled", "&cSorry, autoclaim has been disabled as you are not neighbouring %village%");
        map.put("autoclaim_not_enough_money_disabled", "&cSorry, autoclaim has been disabled as you don't have enough money to claim land");
        map.put("autoclaim_claim_success", "&bYou have claimed [%chunk%] for &9%village% &bfor &9$%cost%");
        map.put("autoclaim_unclaim_success", "&bYou have unclaimed [%chunk%] for &9%village% &bfor a &9$%cost% refund");

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
