package com.johnymuffin.jvillage.beta;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JVUtility {


    public static boolean isAuthorized(CommandSender commandSender, String permission) {
        return (commandSender.isOp() || commandSender.hasPermission(permission));
    }

    public static Player getPlayerFromUUID(UUID uuid) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

}
