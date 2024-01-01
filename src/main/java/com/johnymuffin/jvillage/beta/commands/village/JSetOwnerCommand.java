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

public class JSetOwnerCommand  extends JVBaseCommand implements CommandExecutor {

    public JSetOwnerCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.setowner")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_setowner_use"));
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
            String message = language.getMessage("command_village_setowner_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        String playerName = strings[0];
        UUID uuid = plugin.getFundamentals().getPlayerCache().getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_kick_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        //Check if the target is in the village
        if (!village.isMember(target.getUUID())) {
            String message = language.getMessage("command_village_setowner_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Handle new owner
        village.setOwner(target.getUUID());
        //Message the new owner if they are online
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_village_setowner_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);
        }

        //Handle old owner
        village.addMember(vPlayer.getUUID()); //Add the old owner to the village as a member

        String message = language.getMessage("command_village_setowner_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%player%", target.getUsername());
        commandSender.sendMessage(message);
        return true;
    }
}
