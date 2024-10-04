package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.jvillage.beta.JVUtility.getSafeDestination;

public class JSpawnCommand extends JVBaseCommand implements CommandExecutor {
    public JSpawnCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.spawn")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        //Check if player has a selected village
        Village village;

        if (strings.length > 0) {
            String villageName = strings[0];
            village = plugin.getVillageMap().getVillage(villageName);
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }
            if (!village.isMember(player.getUniqueId()) && !isAuthorized(commandSender, "jvillage.admin.spawn")) {
                String message = language.getMessage("command_village_spawn_not_member");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }
        } else {
            village = vPlayer.getSelectedVillage();
        }


        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        try {
            Location spawnLocation = getSafeDestination(village.getTownSpawn().getLocation());
            player.teleport(spawnLocation);
            String message = language.getMessage("command_village_spawn_success");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        } catch (Exception e) {
            String message = language.getMessage("command_village_spawn_unsafe");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }
    }
}
