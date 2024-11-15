package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JDelWarpCommand extends JVBaseCommand implements CommandExecutor {

    public JDelWarpCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.delwarp")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(language.getMessage("command_village_delwarp_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (!village.isOwner(vPlayer.getUUID()) && !village.isAssistant(vPlayer.getUUID())) {
            String message = language.getMessage("command_village_delwarp_no_permission");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        String warpName = strings[0];

        if (!village.getWarps().containsKey(warpName)) {
            commandSender.sendMessage(language.getMessage("command_village_delwarp_not_found")
                    .replace("%warp%", warpName)
                    .replace("%village%", village.getTownName()));
            return true;
        }

        village.removeWarp(warpName);
        village.broadcastToTown(language.getMessage("command_village_delwarp_del_broadcast")
                .replace("%player%", player.getName())
                .replace("%warp%", warpName)
        );

        return true;
    }
}
