package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.config.JVillageSettings;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.jvillage.beta.JVUtility.formatVillageList;

public class VResidentCommand extends JVBaseCommand {
    private JVillageSettings settings;

    public VResidentCommand(JVillage plugin) {
        super(plugin);
        this.settings = plugin.getSettings();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.resident")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        UUID targetPlayerUUID = null;


        if (strings.length > 0) {
            String targetPlayerName = strings[0];
            targetPlayerUUID = plugin.getUUIDFromUsername(targetPlayerName);
            if (targetPlayerUUID == null) {
                String message = language.getMessage("player_not_found_full");
                message = message.replace("%username%", targetPlayerName);
                commandSender.sendMessage(message);
                return true;
            }
        } else {
            if (commandSender instanceof Player) {
                targetPlayerUUID = ((Player) commandSender).getUniqueId();
            } else {
                commandSender.sendMessage(language.getMessage("unavailable_to_console"));
                return true;
            }
        }

        VPlayer vPlayer = this.plugin.getPlayerMap().getPlayer(targetPlayerUUID);
        Village[] townsOwned = vPlayer.getTownsOwned();
        Village[] townsAssistant = vPlayer.getTownsAssistantOf();
        Village[] townsMember = vPlayer.getTownsMemberOf();

        String menu = language.getMessage("command_resident_info");
        menu = menu.replace("%username%", vPlayer.getUsername());
        //Towns Owned
        if (townsOwned.length > 0) {
            menu = menu.replace("%owner%", formatVillageList(townsOwned));
        } else {
            menu = menu.replace("%owner%", ChatColor.RED + "None");
        }
        //Towns Assisting
        if(townsAssistant.length > 0) {
            menu = menu.replace("%assistant%", formatVillageList(townsAssistant));
        } else {
            menu = menu.replace("%assistant%", ChatColor.RED + "None");
        }

        //Towns Member
        if(townsMember.length > 0) {
            menu = menu.replace("%member%", formatVillageList(townsMember));
        } else {
            menu = menu.replace("%member%", ChatColor.RED + "None");
        }

        menu = menu.replace("%members%", formatVillageList(vPlayer.getTownsMemberOf()));
        sendWithNewline(commandSender, menu);
        return true;
    }
}