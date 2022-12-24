package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.config.JVillageLanguage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class JVillageCommand implements CommandExecutor {
    protected JVillage plugin;

    protected JVillageLanguage language;

    public JVillageCommand(JVillage plugin) {
        this.plugin = plugin;
        this.language = plugin.getLanguage();
    }

    protected boolean isAuthorized(CommandSender commandSender, String permission) {
        return (commandSender.isOp() || commandSender.hasPermission(permission));
    }


    // Remove first entry from string array
    protected String[] removeFirstEntry(String[] strings) {
        String[] newStrings = new String[strings.length - 1];
        System.arraycopy(strings, 1, newStrings, 0, strings.length - 1);
        return newStrings;
    }
}
