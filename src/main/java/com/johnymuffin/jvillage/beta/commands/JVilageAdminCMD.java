package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.events.PlayerJoinVillageEvent;
import com.johnymuffin.jvillage.beta.events.PlayerLeaveVillageEvent;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.jvillage.beta.JVUtility.getChunkCenter;
import static com.johnymuffin.jvillage.beta.JVUtility.getPlayerFromUUID;

public class JVilageAdminCMD extends JVBaseCommand {


    public JVilageAdminCMD(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            //Plugin | World | Village | Player
            if (subcommand.equalsIgnoreCase("plugin")) return pluginCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("village")) return villageCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("world")) return worldCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_general_use"));
        return true;
    }

    private boolean worldCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.world")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("wgcleanup"))
                return worldWGCleanupCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_world_use"));
        return true;
    }

    private boolean worldWGCleanupCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.world.wgcleanup")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        for (VClaim claim : plugin.getAllClaims()) {
            Village village = this.plugin.getVillageMap().getVillage(claim.getVillage());
            //Check if the claim is within a protected worldguard region
            VCords cords = getChunkCenter(claim);
            if (plugin.worldGuardIsClaimAllowed(cords)) {
                continue;
            }
            //Remove the claim from the village
            commandSender.sendMessage("Removing claim " + claim.toString() + " from village " + village.getTownName());
            village.removeClaim(claim);
            //Rum sanity checks to make sure the village is still valid
            if (village.getClaims().size() == 0) {
                String broadcast = language.getMessage("command_villageadmin_village_delete_broadcast");
                broadcast = broadcast.replace("%admin%", commandSender.getName());
                broadcast = broadcast.replace("%village%", village.getTownName());
                Bukkit.broadcastMessage(broadcast);
                commandSender.sendMessage("Village " + village.getTownName() + " has no claims left, removing village");
                this.plugin.deleteVillage(village);
            }

        }
        commandSender.sendMessage(language.getMessage("command_villageadmin_world_wgcleanup_success"));
        return true;

    }

    private boolean villageUnclaim(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village.unclaim")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VChunk vChunk = new VChunk(player.getLocation());

        boolean stillClaimed = true;
        while (stillClaimed) {
            Village village = plugin.getVillageAtLocation(vChunk);
            if (village == null) {
                stillClaimed = false;
                continue;
            }
            village.removeClaim(vChunk);
            //Check if the village is still valid
            if (village.getClaims().size() == 0) {
                String message = language.getMessage("command_villageadmin_village_delete_broadcast");
                message = message.replace("%village%", village.getTownName());
                message = message.replace("%admin%", player.getName());
                Bukkit.broadcastMessage(message);
                plugin.deleteVillage(village);
            }

            String message = language.getMessage("command_villageadmin_village_unclaim_occurrence");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_village_unclaim_success"));
        return true;
    }

    private boolean villageCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            //Add | Remove | Setowner | Delete
            if (subcommand.equalsIgnoreCase("add")) return villageAddCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("setowner"))
                return villageSetOwnerCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("kick"))
                return villageKickCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("delete"))
                return villageDeleteCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("unclaim"))
                return villageUnclaim(commandSender, removeFirstEntry(strings));

        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_village_use"));
        return true;

    }

    private boolean villageDeleteCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village.delete")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }


        if (strings.length < 1) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_delete_use"));
            return true;
        }

        String villageName = strings[0];

        Village village = plugin.getVillageMap().getVillage(villageName);

        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        plugin.deleteVillage(village);

        //Send message to all players
        String broadcastMessage = language.getMessage("command_villageadmin_village_delete_broadcast");
        broadcastMessage = broadcastMessage.replace("%village%", villageName);
        broadcastMessage = broadcastMessage.replace("%admin%", commandSender.getName());
        Bukkit.broadcastMessage(broadcastMessage);


        String message = language.getMessage("command_villageadmin_village_delete_success");
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;
    }

    private boolean villageKickCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village.kick")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }


        if (strings.length < 2) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_kick_use"));
            return true;
        }

        String villageName = strings[0];
        String playerName = strings[1];

        Village village = plugin.getVillageMap().getVillage(villageName);

        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        UUID uuid = plugin.getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("player_not_found_full");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        if (!village.isMember(target.getUUID())) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_kick_not_member"));
            return true;
        }

        if (village.isOwner(target.getUUID())) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_kick_is_owner"));
            return true;
        }

        target.removeVillageMembership(village);
        if (target.getSelectedVillage() == village) {
            target.setSelectedVillage(null);
        }
        village.removeMember(target.getUUID());

        // PlayerLeaveVillageEvent if the player is online
        Player player = getPlayerFromUUID(target.getUUID());
        if(player != null && player.isOnline()) {
            PlayerLeaveVillageEvent event = new PlayerLeaveVillageEvent(player, village);
            Bukkit.getPluginManager().callEvent(event);
        }

        String message = language.getMessage("command_villageadmin_village_kick_success");
        message = message.replace("%player%", playerName);
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;
    }

    private boolean villageSetOwnerCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village.setowner")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_setowner_use"));
            return true;
        }

        String villageName = strings[0];
        String playerName = strings[1];

        Village village = plugin.getVillageMap().getVillage(villageName);

        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        UUID uuid = plugin.getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("player_not_found_full");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);


        if (village.isOwner(target.getUUID())) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_setowner_already_owner"));
            return true;
        }

        //Add user to village if not already in it

        if (!village.isMember(target.getUUID())) {
            village.addMember(target.getUUID());
            target.removeInvitationToVillage(village); //This is just in case they were invited
            target.joinVillage(village);
        }

        //Set the new owner
        UUID oldOwner = village.getOwner();
        village.setOwner(target.getUUID());
        village.addMember(oldOwner);

        String playerMessage = language.getMessage("command_villageadmin_village_setowner_message");
        playerMessage = playerMessage.replace("%admin%", commandSender.getName());
        playerMessage = playerMessage.replace("%player%", playerName);
        playerMessage = playerMessage.replace("%village%", villageName);
        messagePlayers(playerMessage, target.getUUID(), oldOwner);

        String message = language.getMessage("command_villageadmin_village_setowner_success");
        message = message.replace("%player%", playerName);
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;

    }

    private boolean villageAddCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.village.add")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_village_add_use"));
            return true;
        }
        String villageName = strings[0];
        String playerName = strings[1];

        Village village = plugin.getVillageMap().getVillage(villageName);

        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        UUID uuid = plugin.getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("player_not_found_full");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        if (village.isMember(target.getUUID())) {
            String message = language.getMessage("command_villageadmin_village_add_already_member");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        //Add player to village
        village.addMember(target.getUUID());
        target.removeInvitationToVillage(village); //This is just in case they were invited
        target.joinVillage(village);

        //Tell the player they have been added to the village
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_villageadmin_village_add_message");
            message = message.replace("%village%", village.getTownName());
            message = message.replace("%admin%", commandSender.getName());
            targetPlayer.sendMessage(message);
        }

        //Fire PlayerJoinVillageEvent
        PlayerJoinVillageEvent event = new PlayerJoinVillageEvent(targetPlayer, village);
        Bukkit.getPluginManager().callEvent(event);

        String message = language.getMessage("command_villageadmin_village_add_success");
        message = message.replace("%username%", playerName);
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;

    }

    // Plugin Commands
    private boolean pluginCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("reload"))
                return pluginReloadCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.endsWith("import"))
                return pluginImportCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("debug"))
                return pluginToggleDebug(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_use"));
        return true;
    }

    private boolean pluginToggleDebug(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.debug")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        boolean debugMode = plugin.isDebugMode();
        plugin.getSettings().setProperty("settings.debug-mode.enabled", !debugMode);
        plugin.getSettings().save();
        plugin.setDebugMode(!debugMode);

        String message = language.getMessage("command_villageadmin_plugin_debug_change");
        message = message.replace("%state%", !debugMode ? "enabled" : "disabled");
        commandSender.sendMessage(message);

        this.plugin.logger(Level.INFO, "Debug mode is now " + (!debugMode ? "enabled" : "disabled") + " by " + commandSender.getName());
        return true;
    }

    private boolean pluginReloadCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.reload")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //TODO: Implement this command
        commandSender.sendMessage(language.getMessage("generic_not_implemented"));
        return true;
    }

    private boolean pluginImportCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("towny"))
                return pluginImportTownyCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("factions"))
                return pluginImportFactionsCommand(commandSender, removeFirstEntry(strings));
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_use"));
        return true;
    }

    private boolean pluginImportTownyCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import.towny")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_start"));
        if (plugin.townyImport()) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_success"));
        } else {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_towny_fail"));
        }

        return true;

    }

    private boolean pluginImportFactionsCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.admin.plugin.import.factions")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_factions_start"));
        if (plugin.factionsImport()) {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_factions_success"));
        } else {
            commandSender.sendMessage(language.getMessage("command_villageadmin_plugin_import_factions_fail"));
        }

        return true;

    }

}
