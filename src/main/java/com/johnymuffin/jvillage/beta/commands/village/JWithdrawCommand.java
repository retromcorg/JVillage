package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.jvillage.beta.JVUtility;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageFlags;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class JWithdrawCommand extends JVBaseCommand implements CommandExecutor {

    public JWithdrawCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.withdraw")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            sendWithNewline(commandSender, language.getMessage("command_village_withdraw_use"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        String rawAmount = null;

        if (strings.length == 1) {
            //Assume only amount is provided
            rawAmount = strings[0];
        } else {
            //Assume village and amount are provided
            village = plugin.getVillageMap().getVillage(strings[0]);
            rawAmount = strings[1];
        }

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected_or_name_invalid"));
            return true;
        }

        double amount = 0;

        try {
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(language.getMessage("command_village_withdraw_invalid_amount"));
            return true;
        }

        if (amount <= 0) {
            commandSender.sendMessage(language.getMessage("command_village_withdraw_invalid_amount"));
            return true;
        }

        amount = JVUtility.round(amount, 2);

        //Check user has permission to withdraw
        boolean hasPermission = false;

        if (village.isOwner(vPlayer.getUUID())) {
            hasPermission = true;
        }

        if (village.getFlags().get(VillageFlags.ASSISTANT_CAN_WITHDRAW) && village.isAssistant(vPlayer.getUUID())) {
            hasPermission = true;
        }

        if (!hasPermission) {
            commandSender.sendMessage(language.getMessage("command_village_withdraw_no_permission"));
            return true;
        }

        //Check Village has enough money
        if (village.getBalance() < amount) {
            commandSender.sendMessage(language.getMessage("command_village_withdraw_no_funds"));
            return true;
        }

        if (!this.plugin.isFundamentalsEnabled()) {
            sendWithNewline(commandSender, language.getMessage("economy_disabled"));
            return true;
        }

        //Attempt to withdraw money from player
        EconomyAPI.EconomyResult result = FundamentalsAPI.getEconomy().additionBalance(player.getUniqueId(), amount);
        switch (result) {
            case successful:
                village.subtractBalance(amount);
                String message = language.getMessage("command_village_withdraw_success").replace("%amount%", String.valueOf(amount)).replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
                //Send message to all online members
                String broadcast = language.getMessage("command_village_withdraw_broadcast").replace("%amount%", String.valueOf(amount)).replace("%village%", village.getTownName()).replace("%player%", player.getName());
                village.broadcastToTown(broadcast);
                plugin.logger(Level.INFO, "Player " + player.getName() + " withdrew $" + amount + " from the bank of" + village.getTownName());
                return true;
            default:
                commandSender.sendMessage(language.getMessage("generic_error"));
                return true;
        }
    }
}
