package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JDenyCommand extends JVBaseCommand implements CommandExecutor {

    public JDenyCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.deny")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_deny_use"));
            return true;
        }

        String villageName = strings[0];
        Village village = plugin.getVillageMap().getVillage(villageName);
        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        if (!vPlayer.isInvitedToVillage(village)) {
            String message = language.getMessage("command_village_deny_denied");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        vPlayer.removeInvitationToVillage(village);
        String message = language.getMessage("command_village_deny_success");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);

        String broadcast = language.getMessage("command_village_deny_broadcast");
        broadcast = broadcast.replace("%player%", player.getName());
        village.broadcastToTown(broadcast);

        return true;
    }
}
