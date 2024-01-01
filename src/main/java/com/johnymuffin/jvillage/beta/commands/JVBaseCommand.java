package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.config.JVillageLanguage;
import com.johnymuffin.jvillage.beta.config.JVillageSettings;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class JVBaseCommand implements CommandExecutor {
    protected JVillage plugin;

    protected JVillageLanguage language;

    protected JVillageSettings settings;

    public JVBaseCommand(JVillage plugin) {
        this.plugin = plugin;
        this.language = plugin.getLanguage();
        this.settings = plugin.getSettings();
    }

    protected boolean isAuthorized(CommandSender commandSender, String permission) {
        return com.johnymuffin.jvillage.beta.JVUtility.isAuthorized(commandSender, permission);
    }

    // Remove first entry from string array
    protected String[] removeFirstEntry(String[] strings) {
        String[] newStrings = new String[strings.length - 1];
        System.arraycopy(strings, 1, newStrings, 0, strings.length - 1);
        return newStrings;
    }

    protected void sendWithNewline(CommandSender commandSender, String message) {
        String[] lines = message.split("\\n");
        for (String line : lines) {
            commandSender.sendMessage(line);
        }
    }

    public void messagePlayers(String message, UUID... playerUUIDs) {
        for (UUID playerUUID : playerUUIDs) {
            Player player = com.johnymuffin.jvillage.beta.JVUtility.getPlayerFromUUID(playerUUID);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

}
