package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class JDeleteCommand extends JVBaseCommand implements CommandExecutor {

    public JDeleteCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.delete")) {
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
            commandSender.sendMessage(language.getMessage("command_village_delete_use"));
            return true;
        }

        String townName = strings[0];
        Village village = plugin.getVillageMap().getVillage(townName);

        // Check if town with requested name exists
        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        // Check if player is the owner of the town
        if (!village.getOwner().equals(vPlayer.getUUID())) {
            String message = language.getMessage("command_village_delete_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        // Refund the player the balance
        double townBalance = village.getBalance();

        if (townBalance > 0) {
            EconomyAPI.EconomyResult result = FundamentalsAPI.getEconomy().additionBalance(player.getUniqueId(), townBalance);
            String message;
            switch (result) {
                case successful:
                    this.plugin.logger(Level.INFO, "Successfully refunded $" + townBalance + " to " + player.getName() + " for deleting village " + village.getTownName());
                    break;
                default:
                    this.plugin.logger(Level.WARNING, "Failed to refund $" + townBalance + " to " + player.getName() + " for deleting village " + village.getTownName());
                    message = language.getMessage("generic_error");
                    commandSender.sendMessage(message);
                    return true;
            }
        }

        // Delete the town
        plugin.deleteVillage(village);

        //Server broadcast
        String publicMessage = language.getMessage("command_village_delete_broadcast");
        publicMessage = publicMessage.replace("%village%", village.getTownName());
        Bukkit.broadcastMessage(publicMessage);


        String message = language.getMessage("command_village_delete_success");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);
        return true;
    }
}
