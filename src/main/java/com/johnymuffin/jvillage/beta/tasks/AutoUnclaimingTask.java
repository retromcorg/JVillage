package com.johnymuffin.jvillage.beta.tasks;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.config.JVillageLanguage;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.ChunkClaimSettings;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;

import static com.johnymuffin.jvillage.beta.JVUtility.cordsInChunk;
import static com.johnymuffin.jvillage.beta.JVUtility.getChunkCenter;

public class AutoUnclaimingTask implements Runnable {
    private JVillage plugin;

    private JVillageLanguage language;

    public AutoUnclaimingTask(JVillage plugin) {
        this.plugin = plugin;
        language = this.plugin.getLanguage();
    }

    @Override
    public void run() {
        //For each online player
        plugin.debugLogger(Level.INFO, "Running auto unclaiming task");
        for (Player player : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

            //If auto unclaiming is not enabled, skip
            if (!vPlayer.isAutoUnclaimingEnabled()) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " does not have auto unclaiming enabled. Skipping.");
                continue;
            }

            if (vPlayer.getSelectedVillage() == null) {
                //This should never happen but just in case
                vPlayer.setAutoUnclaimingEnabled(false, true);
                this.plugin.logger(Level.WARNING, "Player " + player.getName() + " has auto unclaiming enabled but no village selected. Disabling auto unclaiming.");
                continue;
            }

            Village village = vPlayer.getSelectedVillage();

            //Check player is at least an assistant in the village
            if (!village.isAssistant(player.getUniqueId())) {
                vPlayer.setAutoUnclaimingEnabled(false, true);
                this.plugin.logger(Level.WARNING, "Player " + player.getName() + " has auto unclaiming enabled but is not an assistant in the village. Disabling auto unclaiming.");
                continue;
            }

            // Player is currently located in the wilderness or another village, so can't unclaim
            if (vPlayer.getCurrentlyLocatedIn() == null || vPlayer.getCurrentlyLocatedIn() != vPlayer.getSelectedVillage()) {
                continue;
            }

            VChunk vChunk = new VChunk(player.getLocation());

            if (!this.plugin.worldGuardIsClaimAllowed(getChunkCenter(vChunk))) {
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " is trying to unclaim a protected WorldGuard region. Disabling auto unclaiming.");
                //Claim is protected, disable auto unclaiming, message player and continue
                vPlayer.setAutoUnclaimingEnabled(false, false);
                String message = language.getMessage("autoclaim_enter_worldguard_disabled");
                vPlayer.sendMessage(message);
                continue;
            }

            //Check if the chunk is claimed by the village
            if (!village.isClaimed(vChunk)) {
                String message = language.getMessage("command_village_unclaim_not_claimed");
                message = message.replace("%village%", village.getTownName());
                vPlayer.sendMessage(message);
                continue;
            }

            //Block unclaim if the chunk is the spawn chunk
            VCords spawnCords = village.getTownSpawn();

            if (cordsInChunk(spawnCords, vChunk)) {
                String message = language.getMessage("command_village_unclaim_spawn_block");
                vPlayer.sendMessage(message + "\n");
                vPlayer.setAutoUnclaimingEnabled(false, true);
                continue;
            }

            ChunkClaimSettings chunkClaimSettings = village.getChunkClaimSettings(vChunk);

            //Refund the village the cost of the chunk
            double refund = chunkClaimSettings.getPrice();
            village.addBalance(refund);

            //Unclaim the chunk
            village.removeClaim(new VClaim(village, vChunk));
            String message = language.getMessage("autoclaim_unclaim_success");
            message = message.replace("%village%", village.getTownName());
            message = message.replace("%chunk%", vChunk.toString());
            message = message.replace("%cost%", String.valueOf(refund));
            vPlayer.sendMessage(message);
            plugin.logger(Level.INFO, vChunk.toString() + " unclaimed by " + player.getName() + " for " + village.getTownName() + " with a refund of $" + refund);

            // set player location to wilderness in case they unclaim then stand still
            vPlayer.setCurrentlyLocatedIn(player, null);
        }
    }
}
