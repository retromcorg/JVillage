package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JSelectCommand extends JVBaseCommand implements CommandExecutor {


    public JSelectCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.select")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = null;
        if (strings.length > 0) {
            String villageName = strings[0];
            if (villageName.equalsIgnoreCase("here")) {
                village = vPlayer.getCurrentlyLocatedIn();
            } else {
                village = plugin.getVillageMap().getVillage(villageName);
            }
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }
            //Check if player is in the village
            if (!village.isMember(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("not_in_village"));
                return true;
            }

            vPlayer.setSelectedVillage(village);
            String message = language.getMessage("command_village_select_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        } else {
            //Show what village is selected
            village = vPlayer.getSelectedVillage();
            if (village == null) {
                commandSender.sendMessage(language.getMessage("command_village_select_none"));
                return true;
            } else {
                String message = language.getMessage("command_village_select_use");
                message = message.replace("%village%", ChatColor.RED + village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }
        }
    }
}
