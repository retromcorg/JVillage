package com.johnymuffin.jvillage.beta.tasks;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.ChunkClaimSettings;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.johnymuffin.jvillage.beta.JVUtility.getChunkCenter;

public class AutoClaimingTask implements Runnable {
    private JVillage plugin;

    public AutoClaimingTask(JVillage plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //For each online player
        plugin.debugLogger(Level.INFO, "Running auto claiming task");
        for (Player player : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

            //If auto claiming is not enabled, skip
            if (!vPlayer.isAutoClaimingEnabled()) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " does not have auto claiming enabled. Skipping.");
                continue;
            }

            if (vPlayer.getSelectedVillage() == null) {
                //This should never happen but just in case
                vPlayer.setAutoClaimingEnabled(false, true);
                this.plugin.logger(Level.WARNING, "Player " + player.getName() + " has auto claiming enabled but no village selected. Disabling auto claiming.");
                continue;
            }

            Village village = vPlayer.getSelectedVillage();

            //Check player is at least an assistant in the village
            if (!village.isAssistant(player.getUniqueId())) {
                vPlayer.setAutoClaimingEnabled(false, true);
                this.plugin.logger(Level.WARNING, "Player " + player.getName() + " has auto claiming enabled but is not an assistant in the village. Disabling auto claiming.");
                continue;
            }

            VChunk vChunk = new VChunk(player.getLocation());

            //If player is in a village other than the one they are claiming for disable auto claiming
            if (vPlayer.getCurrentlyLocatedIn() != null && vPlayer.getCurrentlyLocatedIn() != vPlayer.getSelectedVillage()) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is in a village other than the one they are claiming for. Disabling auto claiming.");
                vPlayer.setAutoClaimingEnabled(false, true);
                continue;
            }

            //If player isn't in wilderness, continue. Checking the village isn't required as the above check will catch it
            if (vPlayer.getCurrentlyLocatedIn() != null) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is not in wilderness. Skipping.");
                continue;
            }

            //Player is in wilderness, check if the claim is valid and the village can afford it

            //Check claim isn't a protected WorldGuard region
            if (!this.plugin.worldGuardIsClaimAllowed(getChunkCenter(vChunk))) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is trying to claim a protected WorldGuard region. Disabling auto claiming.");
                //Claim is protected, disable auto claiming, message player and continue
                vPlayer.setAutoClaimingEnabled(false, false);
                String message = this.plugin.getLanguage().getMessage("autoclaim_enter_worldguard_disabled");
                vPlayer.sendMessage(message);
                continue;
            }

            //Check chunk is neighbouring a claimed chunk
            boolean isNeighboring = false;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    VChunk neighbor = new VChunk(vChunk.getWorldName(), vChunk.getX() + i, vChunk.getZ() + j);
                    if (village.isClaimed(neighbor)) {
                        isNeighboring = true;
                        break;
                    }
                }
            }

            if (!isNeighboring) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is trying to claim a chunk that is not neighbouring. Disabling auto claiming.");
                //Claim is not neighbouring, disable auto claiming, message player and continue
                vPlayer.setAutoClaimingEnabled(false, false);
                String message = this.plugin.getLanguage().getMessage("autoclaim_not_neighbouring_disabled");
                message = message.replace("%village%", village.getTownName());
                vPlayer.sendMessage(message);
                continue;
            }

            double creationCost = this.plugin.getSettings().getConfigDouble("settings.town-claim.price.amount");

            //Check village can afford the claim
            if (!village.hasEnough(creationCost)) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is trying to claim a chunk but the village can't afford it. Disabling auto claiming.");
                //Village can't afford the claim, disable auto claiming, message player and continue
                vPlayer.setAutoClaimingEnabled(false, false);
                String message = this.plugin.getLanguage().getMessage("autoclaim_not_enough_money_disabled");
                message = message.replace("%village%", village.getTownName());
                vPlayer.sendMessage(message);
                continue;
            }

            //Remove money from village
            village.subtractBalance(creationCost);


            //Claim is valid, claim the chunk
            village.addClaim(new VClaim(village, vChunk));

            //Metadata for chunk
            ChunkClaimSettings claimSettings = new ChunkClaimSettings(village, System.currentTimeMillis() / 1000L, player.getUniqueId(), vChunk, creationCost);
            village.addChunkClaimMetadata(claimSettings);


            //Message player
            String message = this.plugin.getLanguage().getMessage("autoclaim_claim_success");
            message = message.replace("%village%", village.getTownName());
            message = message.replace("%chunk%", vChunk.toString());
            message = message.replace("%cost%", String.valueOf(creationCost));
            vPlayer.sendMessage(message);

            //Set currently located position so they don't get messaged for a switch back to the village
            vPlayer.setCurrentlyLocatedIn(village);

            plugin.logger(Level.INFO, "Player " + player.getName() + " has auto claimed chunk [" + vChunk.toString() + "] for village " + village.getTownName() + " for $" + creationCost);
        }
    }
}
