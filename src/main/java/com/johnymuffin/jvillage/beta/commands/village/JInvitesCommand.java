package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class JInvitesCommand extends JVBaseCommand implements CommandExecutor {

    public JInvitesCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.invites")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        ArrayList<Village> invites = vPlayer.getInvitedToVillages();

        if (invites.isEmpty()) {
            commandSender.sendMessage(language.getMessage("command_village_invites_no_invites"));
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_village_invites_pending"));
        for (Village village : invites) {
            String message = language.getMessage("command_village_invites_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
        }
        commandSender.sendMessage(language.getMessage("command_village_invites_invite_use"));

        return true;
    }
}
