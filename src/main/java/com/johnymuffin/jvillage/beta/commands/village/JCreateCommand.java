package com.johnymuffin.jvillage.beta.commands.village;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.commands.JVBaseCommand;
import com.johnymuffin.jvillage.beta.models.VSpawnCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.ChunkClaimSettings;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import me.zavdav.zcore.api.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

import static com.johnymuffin.jvillage.beta.JVUtility.getChunkCenter;

public class JCreateCommand extends JVBaseCommand implements CommandExecutor {

    public JCreateCommand(JVillage plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!isAuthorized(commandSender, "jvillage.player.create")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        if (strings.length == 0) {
            commandSender.sendMessage(language.getMessage("command_village_create_use"));
            return true;
        }

        //Check if player has reached max villages owned
        int maxVillages = settings.getConfigInteger("settings.resident.maximum-towns-owned.value");
        if (maxVillages != 0 && vPlayer.getVillageOwnershipCount() >= maxVillages && !isAuthorized(commandSender, "jvillage.bypass.ownerlimit")) {
            String message = language.getMessage("command_village_create_limit");
            message = message.replace("%limit%", String.valueOf(maxVillages));
            commandSender.sendMessage(message);
            return true;
        }

        String villageName = strings[0];

        //If string doesn't only contain numbers and letters
        if (!villageName.matches("[a-zA-Z0-9]+")) {
            commandSender.sendMessage(language.getMessage("command_village_create_invalid_name")
                    .replace("%max%", settings.getConfigInteger("settings.town.max-name-length.value").toString()));
            return true;
        }

        //If string is too long
        if (villageName.length() > settings.getConfigInteger("settings.town.max-name-length.value")) {
            commandSender.sendMessage(language.getMessage("command_village_create_invalid_name")
                    .replace("%max%", settings.getConfigInteger("settings.town.max-name-length.value").toString()));
            return true;
        }

        Village village = plugin.getVillageMap().getVillage(villageName);
        if (village != null) {
            commandSender.sendMessage(language.getMessage("command_village_create_already_exists"));
            return true;
        }

        VChunk vChunk = new VChunk(player.getLocation().getWorld().getName(), player.getLocation().getBlock().getChunk().getX(), player.getLocation().getBlock().getChunk().getZ());

        if (!plugin.worldGuardIsClaimAllowed(getChunkCenter(vChunk))) {
            String message = language.getMessage("command_village_claim_worldguard_denied");
            message = message.replace("%village%", villageName);
            commandSender.sendMessage(message);
            return true;
        }

        if (plugin.isClaimed(vChunk)) {
            commandSender.sendMessage(language.getMessage("command_village_create_already_claimed"));
            return true;
        }

        //Check if another claim exists within a radius
        //TODO: Fix this section of code
        if (settings.getConfigBoolean("settings.town-create.claim-radius.enabled")) {
            int radius = settings.getConfigInteger("settings.town-create.claim-radius.value");
            VClaim[] claims = plugin.getClaimsInRadius(getChunkCenter(vChunk), radius);
            if (claims.length > 0) {
                String message = language.getMessage("command_village_create_too_close");
                message = message.replace("%min%", String.valueOf(radius));
                sendWithNewline(commandSender, message);
                return true;
            }
        }

        //Check if player has enough money
        double creationCost = settings.getConfigDouble("settings.town-create.price.amount");
        if (creationCost > 0 && plugin.isZCoreEnabled()) {
            String message = "";
            try {
                Economy.subtractBalance(player.getUniqueId(), BigDecimal.valueOf(creationCost));
                message = language.getMessage("command_village_create_payment");
                message = message.replace("%amount%", String.valueOf(creationCost));
                message = message.replace("%village%", villageName);
            } catch (Throwable e) {
                if (e.getClass().getName().equals("me.zavdav.zcore.util.NoFundsException")) {
                    message = language.getMessage("command_village_create_insufficient_funds");
                    message = message.replace("%cost%", String.valueOf(creationCost));
                } else {
                    message = language.getMessage("unknown_economy_error");
                }
                return true;
            } finally {
                commandSender.sendMessage(message);
            }
        }

        Village newVillage = new Village(plugin, villageName, UUID.randomUUID(), player.getUniqueId(), vChunk, new VSpawnCords(player.getLocation()));
        plugin.getVillageMap().addVillageToMap(newVillage);

        //Metadata for first chunk
        ChunkClaimSettings claimSettings = new ChunkClaimSettings(newVillage, System.currentTimeMillis() / 1000L, player.getUniqueId(), vChunk, 0);
        newVillage.addChunkClaimMetadata(claimSettings);

        //Manually register the villages first chunk
//        for (VClaim claim : newVillage.getClaims()) {
//            System.out.println("[JVillage] Registering chunk at " + claim.getX() + "," + claim.getZ() + " in world " + claim.getWorldName() + " to village " + newVillage.getTownName());
//            plugin.addClaim(claim);
//        }
//        plugin.loadAllChunks(newVillage);

        vPlayer.setSelectedVillage(newVillage);
        String message = language.getMessage("command_village_create_success");
        message = message.replace("%village%", villageName);
        commandSender.sendMessage(message);

        //Broadcast creation
        String broadcast = language.getMessage("command_village_create_message");
        broadcast = broadcast.replace("%village%", villageName);
        broadcast = broadcast.replace("%player%", player.getName());
        Bukkit.broadcastMessage(broadcast);
        return true;
    }
}
