package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JVillageCMD extends JVBaseCommand {

    public JVillageCMD(JVillage plugin) {
        super(plugin);
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
            if (subcommand.equalsIgnoreCase("select"))
                return selectVillageCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("autoswitch"))
                return autoSwitchCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("leave"))
                return leaveCommand(commandSender, removeFirstEntry(strings));
            if (subcommand.equalsIgnoreCase("invite"))
                return inviteCommand(commandSender, removeFirstEntry(strings));
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
            return true;
        }

        commandSender.sendMessage(language.getMessage("command_village_leave_use"));
        return true;
    }

    private boolean helpCommand(CommandSender commandSender, String[] strings) {
//        if (!isAuthorized(commandSender, "jvillage.player.help")) {
//            commandSender.sendMessage(language.getMessage("no_permission"));
//            return true;
//        }

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
