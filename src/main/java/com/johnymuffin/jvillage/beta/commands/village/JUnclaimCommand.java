package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.ChunkClaimSettings;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.johnymuffin.jvillage.beta.JVUtility.cordsInChunk;

public class JUnclaimCommand extends JVBaseCommand implements CommandExecutor {
    public JUnclaimCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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

        //Auto unclaiming toggle
        if (strings.length > 0 &&
                (strings[0].equalsIgnoreCase("auto") || strings[0].equalsIgnoreCase("a") || strings[0].equalsIgnoreCase("ac") || strings[0].equalsIgnoreCase("autoclaim"))
        ) {
            if(!isAuthorized(commandSender, "jvillage.player.unclaim.auto")) {
                commandSender.sendMessage(language.getMessage("no_permission"));
                return true;
            }

            if (vPlayer.isAutoUnclaimingEnabled()) {
                vPlayer.setAutoUnclaimingEnabled(false, false);
                String message = language.getMessage("command_village_claim_autounclaim_off");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
            } else {
                // Disable autoclaim if it is enabled
                if (vPlayer.isAutoClaimingEnabled()) {
                    vPlayer.setAutoClaimingEnabled(false, false);
                }
                vPlayer.setAutoUnclaimingEnabled(true, false);
                String message = language.getMessage("command_village_claim_autounclaim_on");
                message = message.replace("%village%", village.getTownName());
                commandSender.sendMessage(message);
            }
            return true;
        }

        // Player is unclaiming a single chunk (default)

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

        ChunkClaimSettings chunkClaimSettings = village.getChunkClaimSettings(vChunk);

        //Refund the village the cost of the chunk
        double refund = chunkClaimSettings.getPrice();
        village.addBalance(refund);

        //Unclaim the chunk
        village.removeClaim(new VClaim(village, vChunk));
        String message = language.getMessage("command_village_unclaim_success").replace("%village%", village.getTownName()).replace("%refund%", String.valueOf(refund));
        commandSender.sendMessage(message);
        plugin.logger(Level.INFO, vChunk.toString() + " unclaimed by " + player.getName() + " for " + village.getTownName() + " with a refund of $" + refund);
        return true;
    }
}
