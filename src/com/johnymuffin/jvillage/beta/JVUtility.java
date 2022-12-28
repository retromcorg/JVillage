package com.johnymuffin.jvillage.beta;

import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JVUtility {


    public static boolean isAuthorized(CommandSender commandSender, String permission) {
        return (commandSender.isOp() || commandSender.hasPermission(permission));
    }

//    public static Location getChunkCenter(Location location) {
//        return new Location(location.getWorld(), location.getBlockX() + 8, location.getBlockY(), location.getBlockZ() + 8);
//    }

    public static VCords getChunkCenter(VChunk vChunk) {
//        return new Location(Bukkit.getWorld(vChunk.getWorldName()), vChunk.getX() + 8, 0, vChunk.getZ() + 8);
        return new VCords(vChunk.getX() + 8, 0, vChunk.getZ() + 8, vChunk.getWorldName());
    }

    public static Player getPlayerFromUUID(UUID uuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public static String formatVillageList(Village[] villages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < villages.length; i++) {
            stringBuilder.append(villages[i].getTownName());
            if (i < villages.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

}
