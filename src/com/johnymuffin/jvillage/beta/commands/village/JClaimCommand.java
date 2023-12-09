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

import java.util.ArrayList;
import java.util.Iterator;
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

        boolean outpostClaim = false;

        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase("outpost") || strings[0].equalsIgnoreCase("o")) {
                outpostClaim = true;
            }
            //TODO: Implement auto claiming

            //Rectangle claiming
            if (strings[0].equalsIgnoreCase("rectangle") || strings[0].equalsIgnoreCase("rect")) {
                //Redirect to rectangle claiming
                return this.rectangleClaim(commandSender, command, s, removeFirstEntry(strings), player, vPlayer, village);
            }

            //Circle claiming
//            if (strings[0].equalsIgnoreCase("circle") || strings[0].equalsIgnoreCase("circ")) {
//                //Redirect to circle claiming
//                return this.circleClaim(commandSender, command, s, removeFirstEntry(strings), player, vPlayer, village);
//            }

        }

        //Player is claiming a single chunk (default)

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

    private boolean rectangleClaim(CommandSender commandSender, Command command, String s, String[] strings, Player player, VPlayer vPlayer, Village village) {
        if (!isAuthorized(commandSender, "jvillage.player.claim.rectangle")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        //Check size of area rectangle claim was provided
        if (strings.length < 1) {
            commandSender.sendMessage(language.getMessage("command_village_claim_rectangle_not_enough_arguments"));
            return true;
        }

        //Check chunk player is standing in is claimed by the village
        VChunk vChunk = new VChunk(player.getLocation());
        if (!village.isClaimed(vChunk)) {
            commandSender.sendMessage(language.getMessage("command_village_claim_rectangle_not_in_claim"));
            return true;
        }

        String sizeString = strings[0];
        //Check if size is a number
        if (!isNumeric(sizeString)) {
            commandSender.sendMessage(language.getMessage("command_invalid_argument_provide_integer"));
            return true;
        }

        int chunkClaimRadius = Integer.parseInt(sizeString);

        //Check radius is not too big
        if (settings.getConfigBoolean("settings.town-claim.maximum-claim-size.rect.enabled") && chunkClaimRadius > settings.getConfigInteger("settings.town-claim.maximum-claim-size.rect.value")) {
            String message = language.getMessage("command_village_claim_rectangle_too_big");
            message = message.replace("%size%", String.valueOf(settings.getConfigInteger("settings.town-claim.maximum-claim-size.rect.value")));
            commandSender.sendMessage(message);
            return true;
        }

        //Get all chunks to claim
        ArrayList<VChunk> chunksToClaim = new ArrayList<>();
        for (int x = -chunkClaimRadius; x <= chunkClaimRadius; x++) {
            for (int z = -chunkClaimRadius; z <= chunkClaimRadius; z++) {
                VChunk chunk = new VChunk(vChunk.getWorldName(), vChunk.getX() + x, vChunk.getZ() + z);
                chunksToClaim.add(chunk);
            }
        }

        //Check none of the chunks are already claimed by another village
        Iterator<VChunk> iterator = chunksToClaim.iterator();
        while (iterator.hasNext()) {
            VChunk chunk = iterator.next();
            Village claim = plugin.getVillageAtLocation(chunk);

            if (claim != null) {
                // Chunk is already claimed
                if (claim.equals(village)) {
                    // Chunk is claimed by the same village, remove it
                    iterator.remove();
                } else {
                    // Chunk is claimed by another village
                    String message = language.getMessage("command_village_claim_rectangle_already_claimed");
                    message = message.replace("%village%", claim.getTownName());
                    message = message.replace("%chunk%", chunk.toString());
                    commandSender.sendMessage(message);
                    return true;
                }
            }
        }

        if(chunksToClaim.size() == 0) {
            commandSender.sendMessage(language.getMessage("command_village_claim_rectangle_already_claimed"));
            return true;
        }

        //Calculate cost
        double creationCost = settings.getConfigDouble("settings.town-claim.price.amount") * chunksToClaim.size();

        //Check if village has enough money
        if (!village.hasEnough(creationCost)) {
            String message = language.getMessage("command_village_claim_rectangle_insufficient_funds");
            message = message.replace("%cost%", String.valueOf(creationCost));
            message = message.replace("%chunks%", String.valueOf(chunksToClaim.size()));
            sendWithNewline(commandSender, message);
            return true;
        }

        //Claim the chunks
        for (VChunk chunk : chunksToClaim) {
            village.addClaim(new VClaim(village, chunk));
            ChunkClaimSettings claimSettings = new ChunkClaimSettings(village, System.currentTimeMillis() / 1000L, player.getUniqueId(), chunk, settings.getConfigDouble("settings.town-claim.price.amount"));
            village.addChunkClaimMetadata(claimSettings);
        }

        //Subtract the cost from the village balance
        village.subtractBalance(creationCost);

        //Broadcast message to village
        String broadcastMessage = language.getMessage("command_village_claim_rectangle_broadcast");
        broadcastMessage = broadcastMessage.replace("%village%", village.getTownName());
        broadcastMessage = broadcastMessage.replace("%chunks%", String.valueOf(chunksToClaim.size()));
        broadcastMessage = broadcastMessage.replace("%player%", player.getName());
        broadcastMessage = broadcastMessage.replace("%cost%", String.valueOf(creationCost));
        village.broadcastToTown(broadcastMessage);

        //Send message
        String message = language.getMessage("command_village_claim_rectangle_success");
        message = message.replace("%village%", village.getTownName());
        message = message.replace("%cost%", String.valueOf(creationCost));
        message = message.replace("%chunks%", String.valueOf(chunksToClaim.size()));
        commandSender.sendMessage(message);
        plugin.logger(Level.INFO, chunksToClaim.size() + " chunks claimed for " + village.getTownName() + " by " + player.getName() + " for $" + creationCost);
        return true;
    }

//    private boolean circleClaim(CommandSender commandSender, Command command, String s, String[] strings, Player player, VPlayer vPlayer, Village village) {
//        //TODO: Implement circle claiming
//
//        //Check size of area circle claim was provided
//        if (strings.length < 1) {
//            commandSender.sendMessage(language.getMessage("command_village_claim_circle_not_enough_arguments"));
//            return true;
//        }
//        String radiusString = strings[0];
//        //Check if radius is a number
//        if (!isNumeric(radiusString)) {
//            commandSender.sendMessage(language.getMessage("command_invalid_argument_provide_integer"));
//            return true;
//        }
//
//        return false;
//    }

    //Check if a string is an integer
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
