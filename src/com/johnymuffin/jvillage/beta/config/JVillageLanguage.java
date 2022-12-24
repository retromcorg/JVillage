package com.johnymuffin.jvillage.beta.config;

import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class JVillageLanguage extends Configuration {
    private HashMap<String, String> map;

    public JVillageLanguage(File file) {
        super(file);
        map = new HashMap<String, String>();
        loadDefaults();
        loadFile();
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

        map.put("movement_village_enter", "&bYou have entered the village of &9%village%");
        map.put("movement_wilderness_enter", "&bYou have entered the wilderness");

        map.put("build_denied", "&4Sorry, you don't have permission to build in &9%village%");
        map.put("destroy_denied", "&4Sorry, you don't have permission to destroy in &9%village%");
        map.put("ignite_denied", "&4Sorry, you don't have permission to \"ignite\" in &9%village% :(");

        //JVillage Admin command
        map.put("command_villageadmin_general_use", "&cSorry, that is invalid. Try /villageadmin (plugin|world|village|player)");
        map.put("command_villageadmin_plugin_use", "&cSorry, that is invalid. Try /villageadmin plugin (reload|save|version|import)");
        map.put("command_villageadmin_plugin_import_use", "&cSorry, that is invalid. Try /villageadmin plugin import (towny|factions)");

        map.put("command_villageadmin_plugin_import_towny_start", "&bImporting Towny data. The server might freeze while this is happening.");
        map.put("command_villageadmin_plugin_import_towny_success", "&bImporting Towny data completed successfully. The debug is available in the console.");
        map.put("command_villageadmin_plugin_import_towny_fail", "&bImporting Towny data failed. The debug is available in the console.");

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
