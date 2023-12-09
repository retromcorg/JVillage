package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVUtility;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JBalanceCommand extends JVBaseCommand implements CommandExecutor {

    public JBalanceCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.balance")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }


        Village village = null;
        if (strings.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(language.getMessage("unavailable_to_console"));
                return true;
            }

            Player player = (Player) commandSender;
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

            village = vPlayer.getSelectedVillage();
        } else {
            String villageName = strings[0];
            village = plugin.getVillageMap().getVillage(villageName);
        }

        if (village == null) {
            sendWithNewline(commandSender, language.getMessage("no_village_selected_or_name_invalid"));
            return true;
        }

        String message = language.getMessage("command_village_balance_message");
        double balance = JVUtility.round(village.getBalance(), 2);
        message = message.replace("%village%", village.getTownName()).replace("%balance%", String.valueOf(balance));
        sendWithNewline(commandSender, message);
        return true;
    }
}
