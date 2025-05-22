package com.johnymuffin.jvillage.beta.commands;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.village.*;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class JVillageCMD extends JVBaseCommand {

    private HashMap<String, CommandExecutor> commandExecutors = new HashMap<>();

    public JVillageCMD(JVillage plugin) {
        super(plugin);
        this.settings = plugin.getSettings();

        registerCommand(new JHelpCommand(plugin), "help", "h");
        registerCommand(new JBalanceCommand(plugin), "balance", "bal");
        registerCommand(new JInfoCommand(plugin), "info");
        registerCommand(new JSelectCommand(plugin), "select", "s");
        registerCommand(new JAutoSwitchCommand(plugin), "autoswitch", "switch", "as");
        registerCommand(new JLeaveCommand(plugin), "leave");
        registerCommand(new JInviteCommand(plugin), "invite");
        registerCommand(new JInvitesCommand(plugin), "invites");
        registerCommand(new JJoinCommand(plugin), "join");
        registerCommand(new JDenyCommand(plugin), "deny");
        registerCommand(new JDeleteCommand(plugin), "delete");
        registerCommand(new JCreateCommand(plugin), "create");
        registerCommand(new JClaimCommand(plugin), "claim", "c");
        registerCommand(new JUnclaimCommand(plugin), "unclaim", "uc");
        registerCommand(new JKickCommand(plugin), "kick");
        registerCommand(new JSpawnCommand(plugin), "spawn", "home");
        registerCommand(new JSetOwnerCommand(plugin), "setowner");
        registerCommand(new JDemoteCommand(plugin), "demote");
        registerCommand(new JPromoteCommand(plugin), "promote");
        registerCommand(new JSetSpawnCommand(plugin), "setspawn");
        registerCommand(new JWarpCommand(plugin), "warp");
        registerCommand(new JSetWarpCommand(plugin), "setwarp");
        registerCommand(new JDelWarpCommand(plugin), "delwarp");
        registerCommand(new JRenameCommand(plugin), "rename");
        registerCommand(new JListCommand(plugin), "list");
        registerCommand(new JFlagCommand(plugin), "flag", "flags", "f");
        registerCommand(new JMapCommand(plugin), "map");
        registerCommand(new JWithdrawCommand(plugin), "withdraw", "with");
        registerCommand(new JDepositCommand(plugin), "deposit", "dep");
    }

    private void registerCommand(CommandExecutor commandExecutor, String... alias) {
        for (String s : alias) {
            commandExecutors.put(s, commandExecutor);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (!isAuthorized(commandSender, "jvillage.player")) {
//            commandSender.sendMessage(language.getMessage("no_permission"));
//            return true;
//        }
        if (strings.length > 0) {
            String subcommand = strings[0];
            //New Command System
            CommandExecutor commandExecutor = commandExecutors.get(subcommand);
            if (commandExecutor != null) {
                return commandExecutor.onCommand(commandSender, command, s, removeFirstEntry(strings));
            }

            //Unknow Command
            commandSender.sendMessage(language.getMessage("command_village_unknown"));
            return true;
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

}
