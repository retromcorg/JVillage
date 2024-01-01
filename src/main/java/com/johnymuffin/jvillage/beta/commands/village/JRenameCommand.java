package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JRenameCommand extends JVBaseCommand implements CommandExecutor {
    public JRenameCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.rename")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_rename_use"));
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
            String message = language.getMessage("command_village_rename_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        String villageName = strings[0];

        if (!villageName.matches("[a-zA-Z0-9]+")) {
            commandSender.sendMessage(language.getMessage("command_village_rename_invalid_name"));
            return true;
        }

        if (villageName.length() > settings.getConfigInteger("settings.town.max-name-length.value")) {
            commandSender.sendMessage(language.getMessage("command_village_rename_invalid_name"));
            return true;
        }

        Village village2 = plugin.getVillageMap().getVillage(villageName);
        if (village2 != null) {
            commandSender.sendMessage(language.getMessage("command_village_rename_already_exists"));
            return true;
        }

        String oldName = village.getTownName();

        //Rename the village
        village.setTownName(villageName);

        //Broadcast the rename
        String message = language.getMessage("command_village_rename_broadcast");
        message = message.replace("%village%", oldName);
        message = message.replace("%new_village%", villageName);
        Bukkit.broadcastMessage(message);

        //Message the player
        message = language.getMessage("command_village_rename_success");
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;
    }
}
