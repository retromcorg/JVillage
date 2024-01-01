package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.jvillage.beta.JVUtility.formatUsernames;

public class JListCommand extends JVBaseCommand implements CommandExecutor {


    public JListCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

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
}
