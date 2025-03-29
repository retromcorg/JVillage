package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.events.PlayerJoinVillageEvent;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JJoinCommand extends JVBaseCommand implements CommandExecutor {
    public JJoinCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.join")) {
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
            commandSender.sendMessage(language.getMessage("command_village_join_use"));
            return true;
        }

        String villageName = strings[0];
        Village village = plugin.getVillageMap().getVillage(villageName);
        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        //If player has exceeded the max amount of villages
        int maxVillages = settings.getConfigInteger("settings.resident.maximum-towns-joined.value");
        if (maxVillages != 0 && vPlayer.getVillageMembershipCount() >= maxVillages && !isAuthorized(commandSender, "jvillage.bypass.memberlimit")) {
            String message = language.getMessage("command_village_join_limit");
            message = message.replace("%village%", village.getTownName());
            message = message.replace("%limit%", String.valueOf(maxVillages));
            commandSender.sendMessage(message);
            return true;
        }


        if (vPlayer.isInvitedToVillage(village)) {
            vPlayer.joinVillage(village);
            vPlayer.removeInvitationToVillage(village);
            String message = language.getMessage("command_village_join_success");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);

            //Fire PlayerJoinVillageEvent
            PlayerJoinVillageEvent event = new PlayerJoinVillageEvent(player, village);
            Bukkit.getPluginManager().callEvent(event);

            //Broadcast join
            String broadcast = language.getMessage("command_village_join_broadcast");
            broadcast = broadcast.replace("%player%", player.getName());
            village.broadcastToTown(broadcast);
            return true;
        }

        String message = language.getMessage("command_village_join_denied");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);
        return true;
    }
}
