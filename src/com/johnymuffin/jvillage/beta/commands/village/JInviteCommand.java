package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JInviteCommand extends JVBaseCommand implements CommandExecutor {

    public JInviteCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.invite")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        Village village = vPlayer.getSelectedVillage();
        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (strings.length > 0) {
            String targetName = strings[0];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                String message = language.getMessage("player_not_found_full");
                message = message.replace("%username%", targetName);
                commandSender.sendMessage(message);
                return true;
            }

            VPlayer vTarget = plugin.getPlayerMap().getPlayer(target.getUniqueId());
            if (village.isMember(vTarget.getUUID())) {
                String message = language.getMessage("command_village_invite_already");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }

            //Check if the inviter has the permission to invite
            if (village.isOwner(vPlayer.getUUID()) || village.isAssistant(vPlayer.getUUID()) || village.isMembersCanInvite()) {
                //Send the invite
                vTarget.inviteToVillage(village);
                String message = language.getMessage("command_village_invite_sent");
                message = message.replace("%village%", village.getTownName());
                message = message.replace("%player%", target.getName());
                sendWithNewline(commandSender, message);
                //Message target
                String targetMessage = language.getMessage("command_village_invite_received");
                targetMessage = targetMessage.replace("%village%", village.getTownName());
                sendWithNewline(target, targetMessage);
                //Broadcast
                String broadcast = language.getMessage("command_village_invite_broadcast");
                broadcast = broadcast.replace("%player%", target.getName());
                broadcast = broadcast.replace("%villagemember%", player.getName());
                village.broadcastToTown(broadcast);
                return true;
            }

            String message = language.getMessage("command_village_invite_denied");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        sendWithNewline(commandSender, language.getMessage("command_village_invite_use"));
        return true;
    }
}
