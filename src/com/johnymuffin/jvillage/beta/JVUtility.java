package com.johnymuffin.jvillage.beta;

import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import org.bukkit.Bukkit;
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
        return new VCords(vChunk.getX() * 16 + 8, 0, vChunk.getZ() * 16 + 8, vChunk.getWorldName());
    }

    public static VCords[] getChunkCorners(VChunk vChunk) {
        VCords[] corners = new VCords[4];
        corners[0] = new VCords(vChunk.getX() * 16, 0, vChunk.getZ() * 16, vChunk.getWorldName());
        corners[1] = new VCords(vChunk.getX() * 16 + 15, 0, vChunk.getZ() * 16, vChunk.getWorldName());
        corners[2] = new VCords(vChunk.getX() * 16, 0, vChunk.getZ() * 16 + 15, vChunk.getWorldName());
        corners[3] = new VCords(vChunk.getX() * 16 + 15, 0, vChunk.getZ() * 16 + 15, vChunk.getWorldName());
        return corners;
    }

    public static double distance(VCords cords1, VCords cords2) {
        return Math.sqrt(Math.pow(cords1.getX() - cords2.getX(), 2) + Math.pow(cords1.getZ() - cords2.getZ(), 2));
    }

    public static double distance(VChunk chunk1, VChunk chunk2) {
        return distance(getChunkCenter(chunk1), getChunkCenter(chunk2));
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

    public static String formatUsernames(JVillage plugin, UUID[] players) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < players.length; i++) {
            String username = plugin.getPlayerMap().getPlayer(players[i]).getUsername();

            //Don't show the username if the player username is unknown
            if(username.equalsIgnoreCase("unknown uuid")) {
                continue;
            }

            stringBuilder.append(username);
            if (i < players.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }


}
