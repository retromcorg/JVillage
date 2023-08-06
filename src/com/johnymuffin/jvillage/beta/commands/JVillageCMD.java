package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.config.JVillageSettings;
import com.johnymuffin.jvillage.beta.events.PlayerJoinVillageEvent;
import com.johnymuffin.jvillage.beta.events.PlayerLeaveVillageEvent;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageFlags;
import com.johnymuffin.jvillage.beta.models.chunk.ChunkClaimSettings;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.Utils.getSafeDestination;
import static com.johnymuffin.jvillage.beta.JVUtility.*;

public class JVillageCMD extends JVBaseCommand {
    private JVillageSettings settings;

    public JVillageCMD(JVillage plugin) {
        super(plugin);
        this.settings = plugin.getSettings();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (!isAuthorized(commandSender, "jvillage.player")) {
//            commandSender.sendMessage(language.getMessage("no_permission"));
//            return true;
//        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("help"))
                return helpCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("info"))
                return infoCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("select") || subcommand.equalsIgnoreCase("s"))
                return selectVillageCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("autoswitch") || subcommand.equalsIgnoreCase("switch"))
                return autoSwitchCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("leave"))
                return leaveCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("invite"))
                return inviteCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("join"))
                return joinCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("delete"))
                return deleteCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("create"))
                return createCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("claim") || subcommand.equalsIgnoreCase("c"))
                return claimCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("unclaim") || subcommand.equalsIgnoreCase("uc"))
                return unclaimCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("kick"))
                return kickCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("spawn"))
                return spawnCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("setowner"))
                return setOwnerCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("demote"))
                return demoteCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("promote"))
                return promoteCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("setspawn"))
                return setSpawnCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("rename"))
                return renameCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("list"))
                return listCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("flag") || subcommand.equalsIgnoreCase("flags"))
                return flagCommand(commandSender, removeFirstEntry(strings));

        }

        String villageIn = ChatColor.RED + "None";
        String selectedVillage = ChatColor.RED + "None";
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
            if (vPlayer.getSelectedVillage() != null) {
                selectedVillage = ChatColor.YELLOW + vPlayer.getSelectedVillage().getTownName();
            }

            Village village = vPlayer.getCurrentlyLocatedIn();
            if (village != null) {
                villageIn = ChatColor.YELLOW + village.getTownName();
            } else {
                villageIn = ChatColor.DARK_GREEN + "Wilderness";
            }

        }

        String menu = language.getMessage("command_village_use");
        menu = menu.replace("%village%", selectedVillage);
        menu = menu.replace("%villagein%", villageIn);
        sendWithNewline(commandSender, menu);
        return true;
    }

    private boolean flagCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.flag")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //Send help message if no arguments are provided
        if (strings.length == 0) {
            sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
            return true;
        }

        //Validate command sender is a player
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

        //Check if list command
        if (strings[0].equalsIgnoreCase("list")) {
            String message = language.getMessage("command_village_flag_list_use");

            //Generate flags string
            String flags = "";
            for (Map.Entry<VillageFlags, Boolean> entry : village.getFlags().entrySet()) {
                flags += "\n" + entry.getKey() + ": " + (entry.getValue() ? "Enabled" : "Disabled") + ", ";
            }
            flags = flags.substring(0, flags.length() - 2);

            message = message.replace("%flags%", flags);

            sendWithNewline(commandSender, message);
            return true;
        }

        if (strings.length < 2) {
            sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
            return true;
        }

        // See if user specified a flag
        VillageFlags flag = null;
        for (VillageFlags villageFlag : VillageFlags.values()) {
            if (villageFlag.name().equalsIgnoreCase(strings[0]) || villageFlag.name().replace("_", "").equalsIgnoreCase(strings[0])) {
                flag = villageFlag;
                break;
            }
        }

        if (flag == null) {
            commandSender.sendMessage(language.getMessage("command_village_flag_unknown"));
            return true;
        }

        //Validate specified value
        String valueString = strings[1];
        boolean value = false;

        if (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("on") || valueString.equalsIgnoreCase("enable") || valueString.equalsIgnoreCase("enabled") || valueString.equalsIgnoreCase("yes")) {
            value = true;
        } else if (valueString.equalsIgnoreCase("false") || valueString.equalsIgnoreCase("off") || valueString.equalsIgnoreCase("disable") || valueString.equalsIgnoreCase("disabled") || valueString.equalsIgnoreCase("no")) {
            value = false;
        } else {
            commandSender.sendMessage(language.getMessage("command_village_flag_invalid_value"));
            return true;
        }


        //Check if user is assistant or higher
        if (!village.isOwner(player.getUniqueId())) {
            commandSender.sendMessage(language.getMessage("owner_or_higher"));
            return true;
        }

        //Change flag
        village.setFlag(flag, value);

        String message = language.getMessage("command_village_flag_set_success");
        message = message.replace("%flag%", flag.name());
        message = message.replace("%value%", value ? "Enabled" : "Disabled");

        sendWithNewline(commandSender, message);
        return true;
    }

    private boolean listCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.list")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        UUID targetUUID = null;


        if (strings.length == 0) {
            //Get UUID of the issuing player
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(language.getMessage("unavailable_to_console"));
                return true;
            }
            Player player = (Player) commandSender;
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

            Village selectedVillage = vPlayer.getSelectedVillage();
            if (selectedVillage == null) {
                commandSender.sendMessage(language.getMessage("no_village_selected"));
                return true;
            }

            targetUUID = selectedVillage.getTownUUID();

        } else {
            String targetName = strings[0];
            Village village = plugin.getVillageMap().getVillage(targetName);

            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }

            targetUUID = village.getTownUUID();
        }
        Village village = plugin.getVillageMap().getVillage(targetUUID);

        String villageList = language.getMessage("command_village_list_use");

        villageList = villageList.replace("%village%", village.getTownName());

        String ownerUsername = this.plugin.getPlayerMap().getPlayer(village.getOwner()).getUsername();
        villageList = villageList.replace("%owner%", ownerUsername);

        UUID[] assistants = village.getAssistants();
        String assistantList = formatUsernames(plugin, assistants);
        villageList = villageList.replace("%assistants%", assistantList);

        UUID[] members = village.getMembers();
        String memberList = formatUsernames(plugin, members);
        villageList = villageList.replace("%members%", memberList);

        sendWithNewline(commandSender, villageList);
        return true;
    }

    private boolean renameCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.rename")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_rename_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();


        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_rename_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        String villageName = strings[0];

        if (!villageName.matches("[a-zA-Z0-9]+")) {
            commandSender.sendMessage(language.getMessage("command_village_rename_invalid_name"));
            return true;
        }

        if (villageName.length() > settings.getConfigInteger("settings.town.max-name-length.value")) {
            commandSender.sendMessage(language.getMessage("command_village_rename_invalid_name"));
            return true;
        }

        Village village2 = plugin.getVillageMap().getVillage(villageName);
        if (village2 != null) {
            commandSender.sendMessage(language.getMessage("command_village_rename_already_exists"));
            return true;
        }

        String oldName = village.getTownName();

        //Rename the village
        village.setTownName(villageName);

        //Broadcast the rename
        String message = language.getMessage("command_village_rename_broadcast");
        message = message.replace("%village%", oldName);
        message = message.replace("%new_village%", villageName);
        Bukkit.broadcastMessage(message);

        //Message the player
        message = language.getMessage("command_village_rename_success");
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);
        return true;
    }

    private boolean setSpawnCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.setspawn")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        if (!village.getOwner().equals(player.getUniqueId())) {
            String message = language.getMessage("command_village_setspawn_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation());
        if (!village.getClaims().contains(vChunk)) {
            String message = language.getMessage("command_village_setspawn_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VCords cords = new VCords(player.getLocation());
        village.setTownSpawn(cords);
        village.broadcastToTown(player.getDisplayName() + " has set the spawn point to " + cords.toString());
        return true;
    }

    private boolean demoteCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.demote")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_demote_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check if the player is the owner
        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_demote_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Get target players
        String playerName = strings[0];
        UUID uuid = plugin.getFundamentals().getPlayerCache().getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_demote_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the player is in the village
        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        //Check if the target is in the village
        if (!village.isMember(target.getUUID())) {
            String message = language.getMessage("command_village_demote_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the target isn't an assistant
        if (!village.isAssistant(target.getUUID())) {
            String message = language.getMessage("command_village_demote_not_assistant");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        //Promote the player
        village.removeAssistant(target.getUUID());
        village.addMember(target.getUUID());

        //Message player
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_village_demote_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);
        }


        String message = language.getMessage("command_village_demote_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%player%", playerName);
        commandSender.sendMessage(message);
        return true;
    }

    private boolean promoteCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.promote")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_promote_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check if the player is the owner
        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_promote_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Get target players
        String playerName = strings[0];
        UUID uuid = plugin.getFundamentals().getPlayerCache().getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_promote_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the player is in the village
        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        //Check if the target is in the village
        if (!village.isMember(target.getUUID())) {
            String message = language.getMessage("command_village_promote_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Check if the target is already an assistant
        if (village.isAssistant(target.getUUID())) {
            String message = language.getMessage("command_village_promote_already_assistant");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        //Promote the player
        village.removeMember(target.getUUID());
        village.addAssistant(target.getUUID());

        //Message player
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_village_promote_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);
        }


        String message = language.getMessage("command_village_promote_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%player%", playerName);
        commandSender.sendMessage(message);
        return true;
    }

    private boolean setOwnerCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.setowner")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_setowner_use"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = vPlayer.getSelectedVillage();

        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check if the player is the owner
        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_setowner_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        String playerName = strings[0];
        UUID uuid = plugin.getFundamentals().getPlayerCache().getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_kick_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);

        //Check if the target is in the village
        if (!village.isMember(target.getUUID())) {
            String message = language.getMessage("command_village_setowner_not_in_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Handle new owner
        village.setOwner(target.getUUID());
        //Message the new owner if they are online
        Player targetPlayer = getPlayerFromUUID(target.getUUID());
        if (targetPlayer != null) {
            String message = language.getMessage("command_village_setowner_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);
        }

        //Handle old owner
        village.addMember(vPlayer.getUUID()); //Add the old owner to the village as a member

        String message = language.getMessage("command_village_setowner_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%player%", target.getUsername());
        commandSender.sendMessage(message);
        return true;
    }

    private boolean spawnCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.spawn")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        //Check if player has a selected village
        Village village;

        if (strings.length > 0) {
            String villageName = strings[0];
            village = plugin.getVillageMap().getVillage(villageName);
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }
            if (!village.isMember(player.getUniqueId()) && !isAuthorized(commandSender, "jvillage.admin.spawn")) {
                String message = language.getMessage("command_village_spawn_not_member");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }
        } else {
            village = vPlayer.getSelectedVillage();
        }


        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        try {
            Location spawnLocation = getSafeDestination(village.getTownSpawn().getLocation());
            player.teleport(spawnLocation);
            String message = language.getMessage("command_village_spawn_success");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        } catch (Exception e) {
            String message = language.getMessage("command_village_spawm_unsafe");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }
    }

    private boolean kickCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.kick")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_kick_use"));
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

        if (!village.isOwner(player.getUniqueId())) {
            String message = language.getMessage("command_village_kick_denied");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        String playerName = strings[0];
        UUID uuid = plugin.getFundamentals().getPlayerCache().getUUIDFromUsername(playerName);
        if (uuid == null) {
            String message = language.getMessage("command_village_kick_not_found");
            message = message.replace("%player%", playerName);
            commandSender.sendMessage(message);
            return true;
        }

        if (!village.isMember(uuid)) {
            String message = language.getMessage("command_village_kick_not_member");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VPlayer target = plugin.getPlayerMap().getPlayer(uuid);
        target.removeVillageMembership(village);
        if (target.getSelectedVillage() == village) {
            target.setSelectedVillage(null);
        }
        village.removeMember(uuid);
        commandSender.sendMessage(language.getMessage("command_village_kick_success"));

        //Message the player if they are online
        Player targetPlayer = getPlayerFromUUID(uuid);
        if (targetPlayer != null && targetPlayer.isOnline()) {
            String message = language.getMessage("command_village_kick_message");
            message = message.replace("%village%", village.getTownName());
            targetPlayer.sendMessage(message);

            //PlayerLeaveVillageEvent
            PlayerLeaveVillageEvent event = new PlayerLeaveVillageEvent(targetPlayer, village);
            Bukkit.getPluginManager().callEvent(event);
        }
        return true;
    }

    private boolean unclaimCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.unclaim")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        //Check if player has a selected village
        Village village = vPlayer.getSelectedVillage();
        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check member is assistant or higher
        if (!village.isAssistant(player.getUniqueId())) {
            String message = language.getMessage("command_village_unclaim_not_assistant");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        VChunk vChunk = new VChunk(player.getLocation());

        //Check if the chunk is claimed by the village
        if (!village.isClaimed(vChunk)) {
            String message = language.getMessage("command_village_unclaim_not_claimed");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        //Block unclaim if the chunk is the spawn chunk
        VCords spawnCords = village.getTownSpawn();

        if (cordsInChunk(spawnCords, vChunk)) {
            String message = language.getMessage("command_village_unclaim_spawn_block");
            sendWithNewline(commandSender, message);
            return true;
        }

        //Unclaim the chunk
        village.removeClaim(new VClaim(village, vChunk));
        String message = language.getMessage("command_village_unclaim_success");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);
        plugin.logger(Level.INFO, vChunk.toString() + " unclaimed by " + player.getName() + " for " + village.getTownName());
        return true;
    }

    private boolean claimCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.claim")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        boolean outpostClaim = false;

        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase("outpost") || strings[0].equalsIgnoreCase("o")) {
                outpostClaim = true;
            }
            //TODO: Implement auto claiming
            //TODO: Implement radius claiming
        }


        Village village = vPlayer.getSelectedVillage();
        if (village == null) {
            commandSender.sendMessage(language.getMessage("no_village_selected"));
            return true;
        }

        //Check if the player is an assistant
        if (!village.isAssistant(player.getUniqueId())) {
            String message = language.getMessage("command_village_claim_not_assistant");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation());

        if (!plugin.worldGuardIsClaimAllowed(getChunkCenter(vChunk))) {
            String message = language.getMessage("command_village_claim_worldguard_denied");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }


        //Check if the chunk is already claimed
        if (this.plugin.isClaimed(vChunk)) {
            commandSender.sendMessage(language.getMessage("command_village_claim_already_claimed"));
            return true;
        }

        //Check if chunk is neighboring a claimed chunk
        boolean isNeighboring = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                VChunk neighbor = new VChunk(vChunk.getWorldName(), vChunk.getX() + i, vChunk.getZ() + j);
                if (village.isClaimed(neighbor)) {
                    isNeighboring = true;
                    outpostClaim = false;
                    break;
                }
            }
        }

        if (!isNeighboring && !outpostClaim) {
            String message = language.getMessage("command_village_claim_not_neighboring");
            sendWithNewline(commandSender, message);
            return true;
        }


        //Check if player has enough money
        double creationCost;
        if (outpostClaim) {
            creationCost = settings.getConfigDouble("settings.town-claim-outpost.price.amount");
        } else {
            creationCost = settings.getConfigDouble("settings.town-claim.price.amount");
        }


        if (creationCost > 0) {
            EconomyAPI.EconomyResult result = FundamentalsAPI.getEconomy().subtractBalance(player.getUniqueId(), creationCost, player.getWorld().getName());
            String message;
            switch (result) {
                case successful:
                    break;
                case notEnoughFunds:
                    message = language.getMessage("command_village_claim_insufficient_funds");
                    message = message.replace("%cost%", String.valueOf(creationCost));
                    commandSender.sendMessage(message);
                    return true;
                default:
                    message = language.getMessage("unknown_economy_error");
                    commandSender.sendMessage(message);
                    return true;
            }
        }

        //Claim the chunk
        village.addClaim(new VClaim(village, vChunk));

        //Metadata for first chunk
        ChunkClaimSettings claimSettings = new ChunkClaimSettings(village, System.currentTimeMillis() / 1000L, player.getUniqueId(), vChunk);
        village.addChunkClaimMetadata(claimSettings);

        //Send message
        String message = language.getMessage("command_village_claim_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%cost%", String.valueOf(creationCost));
        commandSender.sendMessage(message);
        plugin.logger(Level.INFO, vChunk.toString() + " claimed for " + village.getTownName() + " by " + player.getName() + " for $" + creationCost);
        return true;
    }

    private boolean createCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.create")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_create_use"));
            return true;
        }

        //Check if player has reached max villages owned
        int maxVillages = settings.getConfigInteger("settings.resident.maximum-towns-owned.value");
        if (maxVillages != 0 && vPlayer.getVillageOwnershipCount() >= maxVillages && !isAuthorized(commandSender, "jvillage.bypass.ownerlimit")) {
            String message = language.getMessage("command_village_create_limit");
            message = message.replace("%limit%", String.valueOf(maxVillages));
            commandSender.sendMessage(message);
            return true;
        }

        String villageName = strings[0];

        //If string doesn't only contain numbers and letters
        if (!villageName.matches("[a-zA-Z0-9]+")) {
            commandSender.sendMessage(language.getMessage("command_village_create_invalid_name"));
            return true;
        }

        //If string is too long
        if (villageName.length() > settings.getConfigInteger("settings.town.max-name-length.value")) {
            commandSender.sendMessage(language.getMessage("command_village_create_invalid_name"));
            return true;
        }

        Village village = plugin.getVillageMap().getVillage(villageName);
        if (village != null) {
            commandSender.sendMessage(language.getMessage("command_village_create_already_exists"));
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation().getWorld().getName(), player.getLocation().getBlock().getChunk().getX(), player.getLocation().getBlock().getChunk().getZ());

        if (plugin.isClaimed(vChunk)) {
            commandSender.sendMessage(language.getMessage("command_village_create_already_claimed"));
            return true;
        }

        //Check if another claim exists within a radius
        //TODO: Fix this section of code
        if (settings.getConfigBoolean("settings.town-create.claim-radius.enabled")) {
            int radius = settings.getConfigInteger("settings.town-create.claim-radius.value");
            VClaim[] claims = plugin.getClaimsInRadius(getChunkCenter(vChunk), radius);
            if (claims.length > 0) {
                String message = language.getMessage("command_village_create_too_close");
                message = message.replace("%min%", String.valueOf(radius));
                sendWithNewline(commandSender, message);
                return true;
            }
        }

        //Check if player has enough money
        double creationCost = settings.getConfigDouble("settings.town-create.price.amount");
        if (creationCost > 0) {
            EconomyAPI.EconomyResult result = FundamentalsAPI.getEconomy().subtractBalance(player.getUniqueId(), creationCost, player.getWorld().getName());
            String message;
            switch (result) {
                case successful:
                    message = language.getMessage("command_village_create_payment");
                    message = message.replace("%amount%", String.valueOf(creationCost));
                    message = message.replace("%village%", villageName);
                    commandSender.sendMessage(message);
                    break;
                case notEnoughFunds:
                    message = language.getMessage("command_village_create_insufficient_funds");
                    message = message.replace("%cost%", String.valueOf(creationCost));
                    commandSender.sendMessage(message);
                    return true;
                default:
                    message = language.getMessage("unknown_economy_error");
                    commandSender.sendMessage(message);
                    return true;
            }
        }

        Village newVillage = new Village(plugin, villageName, UUID.randomUUID(), player.getUniqueId(), vChunk, new VCords(player.getLocation()));
        plugin.getVillageMap().addVillageToMap(newVillage);

        //Metadata for first chunk
        ChunkClaimSettings claimSettings = new ChunkClaimSettings(newVillage, System.currentTimeMillis() / 1000L, player.getUniqueId(), vChunk);
        newVillage.addChunkClaimMetadata(claimSettings);

        //Manually register the villages first chunk
//        for (VClaim claim : newVillage.getClaims()) {
//            System.out.println("[JVillage] Registering chunk at " + claim.getX() + "," + claim.getZ() + " in world " + claim.getWorldName() + " to village " + newVillage.getTownName());
//            plugin.addClaim(claim);
//        }
//        plugin.loadAllChunks(newVillage);

        vPlayer.setSelectedVillage(newVillage);
        String message = language.getMessage("command_village_create_success");
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);

        //Broadcast creation
        String broadcast = language.getMessage("command_village_create_message");
        broadcast = broadcast.replace("%village%", villageName);
        broadcast = broadcast.replace("%player%", player.getName());
        Bukkit.broadcastMessage(broadcast);
        return true;
    }

    private boolean deleteCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.delete")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_delete_use"));
            return true;
        }

        String townName = strings[0];
        Village village = plugin.getVillageMap().getVillage(townName);

        // Check if town with requested name exists
        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        // Check if player is the owner of the town
        if (!village.getOwner().equals(vPlayer.getUUID())) {
            String message = language.getMessage("command_village_delete_not_owner");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        // Delete the town
        plugin.deleteVillage(village);

        //Server broadcast
        String publicMessage = language.getMessage("command_village_delete_broadcast");
        publicMessage = publicMessage.replace("%village%", village.getTownName());
        Bukkit.broadcastMessage(publicMessage);


        String message = language.getMessage("command_village_delete_success");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);
        return true;

    }

    private boolean joinCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.join")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_join_use"));
            return true;
        }

        String villageName = strings[0];
        Village village = plugin.getVillageMap().getVillage(villageName);
        if (village == null) {
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        //If player has exceeded the max amount of villages
        int maxVillages = settings.getConfigInteger("settings.resident.maximum-towns-joined.value");
        if (maxVillages != 0 && vPlayer.getVillageMembershipCount() >= maxVillages && !isAuthorized(commandSender, "jvillage.bypass.memberlimit")) {
            String message = language.getMessage("command_village_join_limit");
            message = message.replace("%village%", village.getTownName());
            message = message.replace("%limit%", String.valueOf(maxVillages));
            commandSender.sendMessage(message);
            return true;
        }


        if (vPlayer.isInvitedToVillage(village)) {
            vPlayer.joinVillage(village);
            village.uninvitePlayer(vPlayer.getUUID());
            String message = language.getMessage("command_village_join_success");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);

            //Fire PlayerJoinVillageEvent
            PlayerJoinVillageEvent event = new PlayerJoinVillageEvent(player, village);
            Bukkit.getPluginManager().callEvent(event);

            //Broadcast join
            String broadcast = language.getMessage("command_village_join_broadcast");
            broadcast = broadcast.replace("%player%", player.getName());
            village.broadcastToTown(broadcast);
            return true;
        }

        String message = language.getMessage("command_village_join_denied");
        message = message.replace("%village%", village.getTownName());
        commandSender.sendMessage(message);
        return true;
    }

    private boolean inviteCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.invite")) {
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

        if (strings.length > 0) {
            String targetName = strings[0];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                String message = language.getMessage("player_not_found_full");
                message = message.replace("%username%", targetName);
                commandSender.sendMessage(message);
                return true;
            }

            VPlayer vTarget = plugin.getPlayerMap().getPlayer(target.getUniqueId());
            if (village.isMember(vTarget.getUUID())) {
                String message = language.getMessage("command_village_invite_already");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }

            //Check if the inviter has the permission to invite
            if (village.isOwner(vPlayer.getUUID()) || village.isAssistant(vPlayer.getUUID()) || village.isMembersCanInvite()) {
                //Send the invite
                vTarget.inviteToVillage(village);
                String message = language.getMessage("command_village_invite_sent");
                message = message.replace("%village%", village.getTownName());
                message = message.replace("%player%", target.getName());
                sendWithNewline(commandSender, message);
                //Message target
                String targetMessage = language.getMessage("command_village_invite_received");
                targetMessage = targetMessage.replace("%village%", village.getTownName());
                sendWithNewline(target, targetMessage);
                //Broadcast
                String broadcast = language.getMessage("command_village_invite_broadcast");
                broadcast = broadcast.replace("%player%", target.getName());
                broadcast = broadcast.replace("%villagemember%", player.getName());
                village.broadcastToTown(broadcast);
                return true;
            }

            String message = language.getMessage("command_village_invite_denied");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        }

        sendWithNewline(commandSender, language.getMessage("command_village_invite_use"));
        return true;
    }

    private boolean leaveCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.leave")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length > 0) {
            String villageName = strings[0];
            Village village = plugin.getVillageMap().getVillage(villageName);
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }

            if (!village.isMember(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("not_in_village"));
                return true;
            }

            if (village.getOwner().equals(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("village_owner_leave"));
                return true;
            }

            vPlayer.leaveVillage(village);
            String message = language.getMessage("command_village_leave_success");
            message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);

            //PlayerLeaveVillageEvent
            PlayerLeaveVillageEvent event = new PlayerLeaveVillageEvent(player, village);
            Bukkit.getPluginManager().callEvent(event);

            //Broadcast leave
            String broadcast = language.getMessage("command_village_leave_broadcast");
            broadcast = broadcast.replace("%player%", player.getName());
            village.broadcastToTown(broadcast);
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_village_leave_use"));
        return true;
    }

    private boolean helpCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.help")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("player")) {
                sendWithNewline(commandSender, language.getMessage("command_village_player_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("assistant")) {
                sendWithNewline(commandSender, language.getMessage("command_village_assistant_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("owner")) {
                sendWithNewline(commandSender, language.getMessage("command_village_owner_help"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("flag") || subcommand.equalsIgnoreCase("flags")) {
                sendWithNewline(commandSender, language.getMessage("command_village_flag_help"));
                return true;
            }
        }

        sendWithNewline(commandSender, language.getMessage("command_village_help_use"));
        return true;
    }

    public boolean autoSwitchCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.autoswitch")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length > 0) {
            String subcommand = strings[0];
            if (subcommand.equalsIgnoreCase("on")) {
                vPlayer.setAutoSwitchSelected(true);
                sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_on"));
                return true;
            }
            if (subcommand.equalsIgnoreCase("off")) {
                vPlayer.setAutoSwitchSelected(false);
                sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_off"));
                return true;
            }
            sendWithNewline(commandSender, language.getMessage("command_village_autoswitch_use"));
            return true;
        }
        String message = language.getMessage("command_village_autoswitch_set");
        message = message.replace("%state%", vPlayer.autoSwitchSelected() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off");
        sendWithNewline(commandSender, message);
        return true;
    }

    public boolean selectVillageCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.select")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
        Village village = null;
        if (strings.length > 0) {
            String villageName = strings[0];
            if (villageName.equalsIgnoreCase("here")) {
                village = vPlayer.getCurrentlyLocatedIn();
            } else {
                village = plugin.getVillageMap().getVillage(villageName);
            }
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }
            //Check if player is in the village
            if (!village.isMember(player.getUniqueId())) {
                commandSender.sendMessage(language.getMessage("not_in_village"));
                return true;
            }

            vPlayer.setSelectedVillage(village);
            String message = language.getMessage("command_village_select_village");
            message = message.replace("%village%", village.getTownName());
            commandSender.sendMessage(message);
            return true;
        } else {
            //Show what village is selected
            village = vPlayer.getSelectedVillage();
            if (village == null) {
                commandSender.sendMessage(language.getMessage("command_village_select_none"));
                return true;
            } else {
                String message = language.getMessage("command_village_select_use");
                message = message.replace("%village%", ChatColor.RED + village.getTownName());
                commandSender.sendMessage(message);
                return true;
            }
        }
    }

    private boolean infoCommand(CommandSender commandSender, String[] strings) {
        if (!isAuthorized(commandSender, "jvillage.player.info")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        Village village = null;
        boolean selected = false;
        if (strings.length > 0) {
            String villageName = strings[0];
            if (villageName.equalsIgnoreCase("here") && commandSender instanceof Player) {
                Player player = (Player) commandSender;
                VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
                village = vPlayer.getCurrentlyLocatedIn();
            } else {
                village = plugin.getVillageMap().getVillage(villageName);

            }
        } else {
            //Try to get the selected village if the command sender is a player
            if (commandSender instanceof Player) {
                selected = true;
                Player player = (Player) commandSender;
                VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
                village = vPlayer.getSelectedVillage();
            }
        }

        if (village == null) {
            if (selected) {
                commandSender.sendMessage(language.getMessage("no_village_selected"));
                return true;
            }
            commandSender.sendMessage(language.getMessage("village_not_found"));
            return true;
        }

        String villageInfo = plugin.getLanguage().getMessage("command_village_info_use");
        villageInfo = villageInfo.replace("%village%", village.getTownName());
        villageInfo = villageInfo.replace("%owner%", (PoseidonUUID.getPlayerUsernameFromUUID(village.getOwner()) != null ? PoseidonUUID.getPlayerUsernameFromUUID(village.getOwner()) : ChatColor.RED + "Unknown UUID"));
        villageInfo = villageInfo.replace("%assistants%", village.getAssistants().length + "");
        villageInfo = villageInfo.replace("%members%", village.getMembers().length + "");
        villageInfo = villageInfo.replace("%claims%", village.getTotalClaims() + "");
        villageInfo = villageInfo.replace("%spawn%", village.getTownSpawn().toString());
        sendWithNewline(commandSender, villageInfo);
        return true;
    }

}
