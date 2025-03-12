package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.VSpawnCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JSetWarpCommand extends JVBaseCommand implements CommandExecutor {

    public JSetWarpCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.setwarp")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(language.getMessage("command_village_setwarp_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (!village.isOwner(vPlayer.getUUID()) && !village.isAssistant(vPlayer.getUUID())) {
            String message = language.getMessage("command_village_setwarp_no_permission");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        String warpName = strings[0];

        if (!warpName.matches("[a-zA-Z0-9]+")) {
            commandSender.sendMessage(language.getMessage("command_village_setwarp_invalid_name")
                    .replace("%max%", settings.getConfigInteger("settings.warp.max-name-length.value").toString()));
            return true;
        }

        if (warpName.length() > settings.getConfigInteger("settings.warp.max-name-length.value")) {
            commandSender.sendMessage(language.getMessage("command_village_setwarp_invalid_name")
                    .replace("%max%", settings.getConfigInteger("settings.warp.max-name-length.value").toString()));
            return true;
        }

        if (village.getWarps().containsKey(warpName)) {
            commandSender.sendMessage(language.getMessage("command_village_setwarp_already_exists"));
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation());
        if (!village.getClaims().contains(vChunk)) {
            String message = language.getMessage("command_village_setwarp_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        double creationCost = settings.getConfigDouble("settings.warp.price.amount");
        if (creationCost > 0) {
            if (!village.hasEnough(creationCost)) {
                String message = language.getMessage("command_village_setwarp_insufficient_funds")
                        .replace("%cost%", String.valueOf(creationCost));
                commandSender.sendMessage(message);
                return true;
            }
            village.subtractBalance(creationCost);
        }
        VSpawnCords cords = new VSpawnCords(player.getLocation());
        village.addWarp(warpName, cords);
        village.broadcastToTown(language.getMessage("command_village_setwarp_set_broadcast")
                .replace("%player%", player.getName())
                .replace("%warp%", warpName));
        return true;
    }
}
