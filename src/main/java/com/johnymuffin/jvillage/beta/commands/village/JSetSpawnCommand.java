package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.VSpawnCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JSetSpawnCommand extends JVBaseCommand implements CommandExecutor {

    public JSetSpawnCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.setspawn")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (!village.getOwner().equals(player.getUniqueId())) {
            String message = language.getMessage("command_village_setspawn_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation());
        if (!village.getClaims().contains(vChunk)) {
            String message = language.getMessage("command_village_setspawn_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VSpawnCords cords = new VSpawnCords(player.getLocation());
        village.setTownSpawn(cords);
        village.broadcastToTown(player.getDisplayName() + " has set the spawn point to " + cords.toString());
        return true;
    }
}
