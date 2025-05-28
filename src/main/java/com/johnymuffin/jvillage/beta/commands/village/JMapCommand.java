package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVUtility;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JMapCommand extends JVBaseCommand implements CommandExecutor {

    public JMapCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.map")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        ArrayList<VClaim> allClaims = (ArrayList<VClaim>) plugin.getAllClaims().clone();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            String[] villageMap = createMap(player.getLocation(), allClaims, village);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.sendMessage(plugin.getLanguage().getMessage("command_village_map_use"));
                for (String row : villageMap) {
                    player.sendMessage(row);
                }
            });
        });

        return true;
    }

    private String[] createMap(Location location, List<VClaim> allClaims, Village selectedVillage) {
        int radiusX = Math.max(1, plugin.getSettings().getConfigInteger("settings.map.radius-x"));
        int radiusZ = Math.max(1, plugin.getSettings().getConfigInteger("settings.map.radius-z"));
        radiusX = Math.min(radiusX, 32);
        radiusZ = Math.min(radiusZ, 32);

        Point[][] matrix = JVUtility.getNearbyChunkCoords(location, radiusX, radiusZ);
        List<VClaim> claims = getFilteredClaims(allClaims, matrix);
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        String cursorColor = plugin.getSettings().getConfigString("settings.map.cursor.color");
        String cursorSymbol = plugin.getSettings().getConfigString("settings.map.cursor.symbol");
        String wildernessColor = plugin.getSettings().getConfigString("settings.map.chunk.color.wilderness");
        String currentVillageColor = plugin.getSettings().getConfigString("settings.map.chunk.color.current-village");
        String otherVillagesColor = plugin.getSettings().getConfigString("settings.map.chunk.color.other-villages");

        for (int row = 0; row < matrix.length; row++) {
            String prevColor = "";
            String currentColor = "";

            for (int col = 0; col < matrix[row].length; col++) {
                if (row == radiusZ && col == radiusX) {
                    cursorColor = cursorColor.replaceAll("&([0-9a-f])", "\u00A7$1");
                    sb.append(cursorColor).append(cursorSymbol).append(currentColor);
                    continue;
                }

                Point point = matrix[row][col];
                VClaim claim = claims.stream()
                        .filter(cl -> cl.getX() == point.x && cl.getZ() == point.y)
                        .findFirst()
                        .orElse(null);

                if (claim == null) {
                    currentColor = wildernessColor;
                } else if (claim.getVillage().equals(selectedVillage.getTownUUID())) {
                    currentColor = currentVillageColor;
                } else {
                    currentColor = otherVillagesColor;
                }

                currentColor = currentColor.replaceAll("&([0-9a-f])", "\u00A7$1");

                if (!currentColor.equals(prevColor)) {
                    prevColor = currentColor;
                    sb.append(currentColor);
                }

                sb.append(plugin.getSettings().getConfigString("settings.map.chunk.symbol"));
            }

            lines.add(sb.toString());
            sb.setLength(0);
        }

        return lines.toArray(new String[0]);
    }

    private List<VClaim> getFilteredClaims(List<VClaim> allClaims, Point[][] matrix) {
        ArrayList<Point> coords = new ArrayList<>();
        for (Point[] points : matrix) {
            for (Point point : points) {
                coords.add(point);
            }
        }

        return allClaims.stream()
                .filter(claim -> coords.contains(new Point(claim.getX(), claim.getZ())))
                .collect(Collectors.toList());
    }

}
