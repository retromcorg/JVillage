package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
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

import static com.johnymuffin.jvillage.beta.JVUtility.getChunkCenter;

public class JClaimCommand extends JVBaseCommand implements CommandExecutor {
    public JClaimCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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
//            EconomyAPI.EconomyResult result = FundamentalsAPI.getEconomy().subtractBalance(player.getUniqueId(), creationCost, player.getWorld().getName());
//            String message;
//            switch (result) {
//                case successful:
//                    break;
//                case notEnoughFunds:
//                    message = language.getMessage("command_village_claim_insufficient_funds");
//                    message = message.replace("%cost%", String.valueOf(creationCost));
//                    commandSender.sendMessage(message);
//                    return true;
//                default:
//                    message = language.getMessage("unknown_economy_error");
//                    commandSender.sendMessage(message);
//                    return true;
//            }
            if (!village.hasEnough(creationCost)) {
                String message = language.getMessage("command_village_claim_insufficient_funds").replace("%cost%", String.valueOf(creationCost));
                sendWithNewline(commandSender, message);
                return true;
            }

            village.subtractBalance(creationCost); //Subtract the cost from the village balance
        }

        //Claim the chunk
        village.addClaim(new VClaim(village, vChunk));

        //Metadata for first chunk. Using the normal cost of a chunk at all times prevents exploits with unclaiming outpost chunks which cost more.
        ChunkClaimSettings claimSettings = new ChunkClaimSettings(village, System.currentTimeMillis() / 1000L, player.getUniqueId(), vChunk, settings.getConfigDouble("settings.town-claim.price.amount"));
        village.addChunkClaimMetadata(claimSettings);

        //Send message
        String message = language.getMessage("command_village_claim_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%cost%", String.valueOf(creationCost));
        commandSender.sendMessage(message);
        plugin.logger(Level.INFO, vChunk.toString() + " claimed for " + village.getTownName() + " by " + player.getName() + " for $" + creationCost);
        return true;
    }
}
