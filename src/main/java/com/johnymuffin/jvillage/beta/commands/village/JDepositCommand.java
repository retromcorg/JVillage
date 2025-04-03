package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVUtility;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import me.zavdav.zcore.api.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.logging.Level;

public class JDepositCommand extends JVBaseCommand implements CommandExecutor {

    public JDepositCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.deposit")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            sendWithNewline(commandSender, language.getMessage("command_village_deposit_use"));
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
            commandSender.sendMessage(language.getMessage("command_village_deposit_invalid_amount"));
            return true;
        }

        amount = JVUtility.round(amount, 2);

        if (amount <= 0) {
            commandSender.sendMessage(language.getMessage("command_village_deposit_invalid_amount"));
            return true;
        }

        //Make sure player is a member of the village
        if (!village.isMember(player.getUniqueId())) {
            String message = language.getMessage("command_village_deposit_not_member").replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        if (!this.plugin.isZCoreEnabled()) {
            sendWithNewline(commandSender, language.getMessage("economy_disabled"));
            return true;
        }

        //Attempt to withdraw money from player
        try {
            Economy.subtractBalance(player.getUniqueId(), BigDecimal.valueOf(amount));
            village.addBalance(amount);
            String message = language.getMessage("command_village_deposit_success").replace("%amount%", String.valueOf(amount)).replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            //Send message to all online members
            String broadcast = language.getMessage("command_village_deposit_broadcast").replace("%amount%", String.valueOf(amount)).replace("%village%", village.getTownName()).replace("%player%", player.getName());
            village.broadcastToTown(broadcast);
            plugin.logger(Level.INFO, "Player " + player.getName() + " deposited $" + amount + " into the bank of" + village.getTownName());
        } catch (Throwable e) {
            if (e.getClass().getName().equals("me.zavdav.zcore.util.NoFundsException")) {
                commandSender.sendMessage(language.getMessage("command_village_deposit_no_funds"));
            } else {
                commandSender.sendMessage(language.getMessage("generic_error"));
            }
        }

        return true;
    }
}
