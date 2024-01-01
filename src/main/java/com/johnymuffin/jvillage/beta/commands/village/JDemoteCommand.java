package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.jvillage.beta.JVUtility.getPlayerFromUUID;

public class JDemoteCommand extends JVBaseCommand implements CommandExecutor {

    public JDemoteCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.demote")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_demote_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check if the player is the owner
        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_demote_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Get target players
        String playerName = strings[0];
        UUID uuid = plugin.getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_demote_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the player is in the village
        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        //Check if the target is in the village
        if (!village.isMember(target.getUUID())) {
            String message = language.getMessage("command_village_demote_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the target isn't an assistant
        if (!village.isAssistant(target.getUUID())) {
            String message = language.getMessage("command_village_demote_not_assistant");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        //Promote the player
        village.removeAssistant(target.getUUID());
        village.addMember(target.getUUID());

        //Message player
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_village_demote_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);
        }


        String message = language.getMessage("command_village_demote_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%player%", playerName);
        commandSender.sendMessage(message);
        return true;
    }
}
