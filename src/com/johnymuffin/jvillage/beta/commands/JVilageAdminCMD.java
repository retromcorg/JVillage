package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class JVilageAdminCMD extends JVillageCommand {


    public JVilageAdminCMD(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            //Plugin | World | Village | Player
            if (subcommand.equalsIgnoreCase("plugin")) return pluginCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_general_use"));
        return true;
    }

    // Plugin Commands

    private boolean pluginCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("reload"))
                return pluginReloadCommand(commandSender, removeFirstEntry(strings));
            if(subcommand.endsWith("import"))
                return pluginImportCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_use"));
        return true;
    }

    private boolean pluginReloadCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.reload")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //TODO: Implement this command
        commandSender.sendMessage(language.getMessage("generic_not_implemented"));
        return true;
    }

    private boolean pluginImportCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("towny"))
                return pluginImportTownyCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("factions"))
                return pluginImportFactionsCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_use"));
        return true;
    }

    private boolean pluginImportTownyCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import.towny")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_start"));
        if (plugin.townyImport()) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_success"));
        } else {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_fail"));
        }

        return true;

    }

    private boolean pluginImportFactionsCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import.factions")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //TODO: Implement this command
        commandSender.sendMessage(language.getMessage("generic_not_implemented"));
        return true;
    }

}
