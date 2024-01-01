package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JAutoSwitchCommand extends JVBaseCommand implements CommandExecutor {


    public JAutoSwitchCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.autoswitch")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("on")) {
                vPlayer.setAutoSwitchSelected(true);
                sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_on"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("off")) {
                vPlayer.setAutoSwitchSelected(false);
                sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_off"));
                return true;
            }
            sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_use"));
            return true;
        }
        String message = language.getMessage("command_village_autoswitch_set");
        message = message.replace("%state%", vPlayer.autoSwitchSelected() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off");
        sendWithNewline(commandSender, message);
        return true;
    }
}
