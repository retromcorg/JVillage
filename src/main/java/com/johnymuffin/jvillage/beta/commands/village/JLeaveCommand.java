package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.events.PlayerLeaveVillageEvent;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JLeaveCommand extends JVBaseCommand implements CommandExecutor {

    public JLeaveCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.leave")) {
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
            String villageName = strings[0];
            Village village = plugin.getVillageMap().getVillage(villageName);
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }

            if (!village.isMember(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("not_in_village"));
                return true;
            }

            if (village.getOwner().equals(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("village_owner_leave"));
                return true;
            }

            vPlayer.leaveVillage(village);
            String message = language.getMessage("command_village_leave_success");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);

            //PlayerLeaveVillageEvent
            PlayerLeaveVillageEvent event = new PlayerLeaveVillageEvent(player, village);
            Bukkit.getPluginManager().callEvent(event);

            //Broadcast leave
            String broadcast = language.getMessage("command_village_leave_broadcast");
            broadcast = broadcast.replace("%player%", player.getName());
            village.broadcastToTown(broadcast);
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_village_leave_use"));
        return true;
    }
}
