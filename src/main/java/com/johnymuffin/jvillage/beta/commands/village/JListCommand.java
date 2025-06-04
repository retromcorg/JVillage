package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        Village village = null;
        int page = 1;

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());
            Village selectedVillage = vPlayer.getSelectedVillage();

            if (strings.length == 0) {
                if (selectedVillage == null) {
                    commandSender.sendMessage(language.getMessage("no_village_selected"));
                    return true;
                }
                village = selectedVillage;
            } else {
                try {
                    page = Integer.parseInt(strings[0]);
                    village = selectedVillage;
                    if (strings.length > 1) {
                        commandSender.sendMessage(language.getMessage("too_many_arguments"));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    village = plugin.getVillageMap().getVillage(strings[0]);
                    if (village == null) {
                        commandSender.sendMessage(language.getMessage("village_not_found"));
                        return true;
                    }
                    if (strings.length >= 2) {
                        try {
                            page = Integer.parseInt(strings[1]);
                        } catch (NumberFormatException ex) {
                            commandSender.sendMessage(language.getMessage("invalid_page_number"));
                            return true;
                        }
                    }
                }
            }
        } else {
            if (strings.length == 0) {
                commandSender.sendMessage(language.getMessage("unavailable_to_console"));
                return true;
            }
            village = plugin.getVillageMap().getVillage(strings[0]);
            if (village == null) {
                commandSender.sendMessage(language.getMessage("village_not_found"));
                return true;
            }
            if (strings.length >= 2) {
                try {
                    page = Integer.parseInt(strings[1]);
                } catch (NumberFormatException ex) {
                    commandSender.sendMessage(language.getMessage("invalid_page_number"));
                    return true;
                }
            }
        }

        // Process members with duplicate protection
        List<UUID> uniqueMembers = new ArrayList<>();
        Set<UUID> seenUUIDs = new HashSet<>();
        UUID[] originalMembers = village.getMembers();

        for (UUID uuid : originalMembers) {
            if (uuid != null && !seenUUIDs.contains(uuid)) {
                seenUUIDs.add(uuid);
                uniqueMembers.add(uuid);
            }
        }

        UUID[] filteredMembers = uniqueMembers.toArray(new UUID[0]);
        int pageSize = 15;
        int totalMembers = filteredMembers.length;
        int totalPages = (int) Math.ceil((double) totalMembers / pageSize);
        totalPages = Math.max(1, totalPages);

        if (page < 1 || page > totalPages) {
            commandSender.sendMessage(language.getMessage("invalid_page_number")
                    .replace("%max%", String.valueOf(totalPages)));
            return true;
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalMembers);
        UUID[] pageMembers = Arrays.copyOfRange(filteredMembers, start, end);
        String memberList = formatUsernames(plugin, pageMembers);

        String ownerUsername = plugin.getPlayerMap().getPlayer(village.getOwner()).getUsername();
        UUID[] assistants = village.getAssistants();
        String assistantList = formatUsernames(plugin, assistants);

        // Builds the message
        String villageList = language.getMessage("command_village_list_use")
                .replace("%village%", village.getTownName())
                .replace("%owner%", ownerUsername)
                .replace("%assistants%", assistantList)
                .replace("%members%", memberList);

        // Add page footer with page numbers
        if (totalPages > 1) {
            String pageHint = language.getMessage("multiple_pages")
                    .replace("%current_page%", String.valueOf(page))
                    .replace("%total_pages%", String.valueOf(totalPages));
            villageList += "\n" + pageHint;
        }

        sendWithNewline(commandSender, villageList);
        return true;
    }
}