package com.johnymuffin.jvillage.beta;

import org.bukkit.command.CommandSender;

public class JVUtility {


    public static boolean isAuthorized(CommandSender commandSender, String permission) {
        return (commandSender.isOp() || commandSender.hasPermission(permission));
    }

}
