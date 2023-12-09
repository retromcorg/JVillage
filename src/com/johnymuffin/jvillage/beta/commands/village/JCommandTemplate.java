package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JCommandTemplate extends JVBaseCommand implements CommandExecutor {
    //This is a template for a command. Copy this file and rename it to the command you want to make.
    //This is just here for convenience, you can delete this file if you want.

    public JCommandTemplate(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
