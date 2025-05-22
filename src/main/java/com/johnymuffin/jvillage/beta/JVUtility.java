package com.johnymuffin.jvillage.beta;

import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    public static boolean cordsInChunk(VCords cords, VChunk vChunk) {
        return cords.getX() >= vChunk.getX() * 16 && cords.getX() <= vChunk.getX() * 16 + 15 && cords.getZ() >= vChunk.getZ() * 16 && cords.getZ() <= vChunk.getZ() * 16 + 15;
    }

    public static VCords[] getChunkCorners(VChunk vChunk) {
        VCords[] corners = new VCords[4];
        corners[0] = new VCords(vChunk.getX() * 16, 0, vChunk.getZ() * 16, vChunk.getWorldName());
        corners[1] = new VCords(vChunk.getX() * 16 + 15, 0, vChunk.getZ() * 16, vChunk.getWorldName());
        corners[2] = new VCords(vChunk.getX() * 16, 0, vChunk.getZ() * 16 + 15, vChunk.getWorldName());
        corners[3] = new VCords(vChunk.getX() * 16 + 15, 0, vChunk.getZ() * 16 + 15, vChunk.getWorldName());
        return corners;
    }

    public static Point[][] getNearbyChunkCoords(Location location, int radiusX, int radiusZ) {
        Point[][] chunkCoords = new Point[2 * radiusZ + 1][2 * radiusX + 1];
        int centerX = location.getBlockX() >> 4;
        int centerZ = location.getBlockZ() >> 4;
        Direction direction = Direction.byYaw(location.getYaw());

        for (int row = -radiusZ; row <= radiusZ; row++) {
            for (int col = -radiusX; col <= radiusX ; col++) {
                int dx = col;
                int dz = row;
                switch (direction) {
                    case SOUTH:
                        break;
                    case WEST:
                        dx = -row;
                        dz = col;
                        break;
                    case NORTH:
                        dx = -col;
                        dz = -row;
                        break;
                    case EAST:
                        dx = row;
                        dz = -col;
                        break;
                }

                chunkCoords[row + radiusZ][col + radiusX] = new Point(centerX + dx, centerZ + dz);
            }
        }

        return chunkCoords;
    }

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        static Direction byYaw(double yaw) {
            yaw = (yaw % 360 + 360) % 360;
            if (yaw >= 45 && yaw < 135) return Direction.EAST;
            else if (yaw >= 135 && yaw < 225) return Direction.SOUTH;
            else if (yaw >= 225 && yaw < 315) return Direction.WEST;
            else return Direction.NORTH;
        }
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int closestYaw(double yaw){
        if (yaw < 0) yaw = 360 + yaw;
        Set<Integer> yaws = new HashSet<>(Arrays.asList(0, 90, 180, 270, 360));
        int closest = -1;
        double lowestDiff = Integer.MAX_VALUE;

        for (Integer entry : yaws) {
            double diff = Math.abs(yaw - entry);
            if (diff < lowestDiff) {
                closest = entry;
                lowestDiff = diff;
            }
        }
        if (closest == 360) return 0;
        return closest;
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

    public static UUID getUUIDFromPoseidonCache(String username) {
        UUID uuid = PoseidonUUID.getPlayerUUIDFromCache(username, true);

        if(uuid == null) {
            uuid = PoseidonUUID.getPlayerUUIDFromCache(username, false);
        }

        return uuid;
    }

    //Essentials Code Start: com.earth2me.essentials.Util
    private static final Set<Integer> AIR_MATERIALS = new HashSet<Integer>();

    static {
        AIR_MATERIALS.add(Material.AIR.getId());
        AIR_MATERIALS.add(Material.SAPLING.getId());
        AIR_MATERIALS.add(Material.POWERED_RAIL.getId());
        AIR_MATERIALS.add(Material.DETECTOR_RAIL.getId());
        AIR_MATERIALS.add(Material.DEAD_BUSH.getId());
        AIR_MATERIALS.add(Material.RAILS.getId());
        AIR_MATERIALS.add(Material.YELLOW_FLOWER.getId());
        AIR_MATERIALS.add(Material.RED_ROSE.getId());
        AIR_MATERIALS.add(Material.RED_MUSHROOM.getId());
        AIR_MATERIALS.add(Material.BROWN_MUSHROOM.getId());
        AIR_MATERIALS.add(Material.SEEDS.getId());
        AIR_MATERIALS.add(Material.SIGN_POST.getId());
        AIR_MATERIALS.add(Material.WALL_SIGN.getId());
        AIR_MATERIALS.add(Material.LADDER.getId());
        AIR_MATERIALS.add(Material.SUGAR_CANE_BLOCK.getId());
        AIR_MATERIALS.add(Material.REDSTONE_WIRE.getId());
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_OFF.getId());
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_ON.getId());
        AIR_MATERIALS.add(Material.TORCH.getId());
        AIR_MATERIALS.add(Material.SOIL.getId());
        AIR_MATERIALS.add(Material.DIODE_BLOCK_OFF.getId());
        AIR_MATERIALS.add(Material.DIODE_BLOCK_ON.getId());
        AIR_MATERIALS.add(Material.TRAP_DOOR.getId());
        AIR_MATERIALS.add(Material.STONE_BUTTON.getId());
        AIR_MATERIALS.add(Material.STONE_PLATE.getId());
        AIR_MATERIALS.add(Material.WOOD_PLATE.getId());
        AIR_MATERIALS.add(Material.IRON_DOOR_BLOCK.getId());
        AIR_MATERIALS.add(Material.WOODEN_DOOR.getId());
    }

    public static Location getSafeDestination(final Location loc) throws Exception {
        if (loc == null || loc.getWorld() == null) {
            throw new Exception("Invalid Location Object");
        }
        final World world = loc.getWorld();
        int x = (int) Math.round(loc.getX());
        int y = (int) Math.round(loc.getY());
        int z = (int) Math.round(loc.getZ());

        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1;
            if (y < 0) {
                break;
            }
        }

        while (isBlockUnsafe(world, x, y, z)) {
            y += 1;
            if (y >= 127) {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y -= 1;
            if (y <= 1) {
                y = 127;
                x += 1;
                if (x - 32 > loc.getBlockX()) {
                    throw new Exception("Sorry, there is a hole in the floor");
                }
            }
        }
        return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(), loc.getPitch());
    }

    private static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
        return AIR_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType().getId());
    }

    private static boolean isBlockUnsafe(final World world, final int x, final int y, final int z) {
        final Block below = world.getBlockAt(x, y - 1, z);
        if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA) {
            return true;
        }

        if (below.getType() == Material.FIRE) {
            return true;
        }

        if ((!AIR_MATERIALS.contains(world.getBlockAt(x, y, z).getType().getId()))
                || (!AIR_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType().getId()))) {
            return true;
        }
        return isBlockAboveAir(world, x, y, z);
    }
    //Essentials Code End


}
