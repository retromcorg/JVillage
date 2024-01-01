package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JHelpCommand extends JVBaseCommand implements CommandExecutor {

    public JHelpCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.help")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("player")) {
                sendWithNewline(commandSender, language.getMessage("command_village_player_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("assistant")) {
                sendWithNewline(commandSender, language.getMessage("command_village_assistant_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("owner")) {
                sendWithNewline(commandSender, language.getMessage("command_village_owner_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("flag") || subcommand.equalsIgnoreCase("flags")) {
                sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
                return true;
            }
        }

        sendWithNewline(commandSender, language.getMessage("command_village_help_use"));
        return true;
    }
}
