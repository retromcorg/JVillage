package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageFlags;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class JFlagCommand extends JVBaseCommand implements CommandExecutor {
    public JFlagCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.flag")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //Send help message if no arguments are provided
        if (strings.length == 0) {
            sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
            return true;
        }

        //Validate command sender is a player
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

        //Check if list command
        if (strings[0].equalsIgnoreCase("list")) {
            String message = language.getMessage("command_village_flag_list_use");

            //Generate flags string
            String flags = "";
            for (Map.Entry<VillageFlags, Boolean> entry : village.getFlags().entrySet()) {
                flags += "\n" + entry.getKey() + ": " + (entry.getValue() ? "Enabled" : "Disabled") + ", ";
            }
            flags = flags.substring(0, flags.length() - 2);

            message = message.replace("%flags%", flags);

            sendWithNewline(commandSender, message);
            return true;
        }

        if (strings.length < 2) {
            sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
            return true;
        }

        // See if user specified a flag
        VillageFlags flag = null;
        for (VillageFlags villageFlag : VillageFlags.values()) {
            if (villageFlag.name().equalsIgnoreCase(strings[0]) || villageFlag.name().replace("_", "").equalsIgnoreCase(strings[0])) {
                flag = villageFlag;
                break;
            }
        }

        if (flag == null) {
            commandSender.sendMessage(language.getMessage("command_village_flag_unknown"));
            return true;
        }

        //Validate specified value
        String valueString = strings[1];
        boolean value = false;

        if (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("on") || valueString.equalsIgnoreCase("enable") || valueString.equalsIgnoreCase("enabled") || valueString.equalsIgnoreCase("yes")) {
            value = true;
        } else if (valueString.equalsIgnoreCase("false") || valueString.equalsIgnoreCase("off") || valueString.equalsIgnoreCase("disable") || valueString.equalsIgnoreCase("disabled") || valueString.equalsIgnoreCase("no")) {
            value = false;
        } else {
            commandSender.sendMessage(language.getMessage("command_village_flag_invalid_value"));
            return true;
        }


        //Check if user is assistant or higher
        if (!village.isOwner(player.getUniqueId())) {
            commandSender.sendMessage(language.getMessage("owner_or_higher"));
            return true;
        }

        //Change flag
        village.setFlag(flag, value);

        String message = language.getMessage("command_village_flag_set_success");
        message = message.replace("%flag%", flag.name());
        message = message.replace("%value%", value ? "Enabled" : "Disabled");

        sendWithNewline(commandSender, message);
        return true;
    }
}
