package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.jvillage.beta.JVUtility.round;

public class JInfoCommand extends JVBaseCommand implements CommandExecutor {

    public JInfoCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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
        villageInfo = villageInfo.replace("%balance%", round(village.getBalance(), 2) + "");
        sendWithNewline(commandSender, villageInfo);
        return true;
    }
}
