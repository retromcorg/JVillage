package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.jvillage.beta.JVUtility.getSafeDestination;

public class JWarpCommand extends JVBaseCommand implements CommandExecutor {

    public JWarpCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.warp")) {
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

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_warp_list")
                    .replace("%village%", village.getTownName()));
            commandSender.sendMessage(ChatColor.GRAY + String.join(", ", village.getWarps().keySet()));
            return true;
        }

        String warpName = strings[0];

        if (!village.getWarps().containsKey(warpName)) {
            commandSender.sendMessage(language.getMessage("command_village_warp_not_found")
                    .replace("%warp%", warpName)
                    .replace("%village%", village.getTownName()));
            return true;
        }

        Location location = village.getWarps().get(warpName).getLocation();
        try {
            Location spawnLocation = getSafeDestination(location);
            player.teleport(spawnLocation);
            commandSender.sendMessage(language.getMessage("command_village_warp_success")
                    .replace("%warp%", warpName));
            return true;
        } catch (Exception e) {
            String message = language.getMessage("command_village_warp_unsafe");
            message = message.replace("%warp%", warpName);
            commandSender.sendMessage(message);
            return true;
        }
    }
}
