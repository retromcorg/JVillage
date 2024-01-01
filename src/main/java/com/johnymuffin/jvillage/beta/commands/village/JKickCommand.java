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

import java.util.UUID;

import static com.johnymuffin.jvillage.beta.JVUtility.getPlayerFromUUID;

public class JKickCommand extends JVBaseCommand implements CommandExecutor {
    public JKickCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.kick")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_kick_use"));
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

        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_kick_denied");
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

        if (!village.isMember(uuid)) {
            String message = language.getMessage("command_village_kick_not_member");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);
        target.removeVillageMembership(village);
        if (target.getSelectedVillage() == village) {
            target.setSelectedVillage(null);
        }
        village.removeMember(uuid);
        commandSender.sendMessage(language.getMessage("command_village_kick_success"));

        //Message the player if they are online
        Player targetPlayer = getPlayerFromUUID(uuid);
        if (targetPlayer != null && targetPlayer.isOnline()) {
            String message = language.getMessage("command_village_kick_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);

            //PlayerLeaveVillageEvent
            PlayerLeaveVillageEvent event = new PlayerLeaveVillageEvent(targetPlayer, village);
            Bukkit.getPluginManager().callEvent(event);
        }
        return true;
    }
}
