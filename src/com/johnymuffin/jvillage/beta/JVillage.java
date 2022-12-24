package com.johnymuffin.jvillage.beta;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.jvillage.beta.commands.JVilageAdminCMD;
import com.johnymuffin.jvillage.beta.commands.JVillageCMD;
import com.johnymuffin.jvillage.beta.config.JVillageLanguage;
import com.johnymuffin.jvillage.beta.interfaces.ClaimManager;
import com.johnymuffin.jvillage.beta.listeners.JVMobListener;
import com.johnymuffin.jvillage.beta.listeners.JVPlayerAlterListener;
import com.johnymuffin.jvillage.beta.listeners.JVPlayerMoveListener;
import com.johnymuffin.jvillage.beta.maps.JPlayerMap;
import com.johnymuffin.jvillage.beta.maps.JVillageMap;
import com.johnymuffin.jvillage.beta.models.*;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JVillage extends JavaPlugin implements ClaimManager {
    //Basic Plugin Info
    private static JVillage plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //    private HashMap<VillageEntry, Village> villages = new HashMap<>(); //Main list for all villages
//    private HashMap<String, WorldClaimManager> claims = new HashMap<>();
    private ArrayList<VClaim> claims = new ArrayList<>();

    private JVillageLanguage language;

    private boolean errored = false;

    private JVillageMap villageMap;
    private JPlayerMap playerMap;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        //Config files
        language = new JVillageLanguage(new File(this.getDataFolder(), "language.yml"));

        //Generate WorldCLaimManagers
//        for (World world : Bukkit.getWorlds()) {
////            claims.put(world.getName(), new WorldClaimManager(plugin, world.getName()));
//            getWorldClaimManager(world.getName(), true);
//        }

        //Load villages
        int villagesLoaded = 0;
        int claimsLoaded = 0;
        villageMap = new JVillageMap(this);

        //Generate memory claim cache
        for (UUID villageUUID : villageMap.getKnownVillages()) {
            Village village = villageMap.getVillage(villageUUID);
            villagesLoaded++;
            int claims = loadAllChunks(village);
            claimsLoaded += claims;

//            for (String world : village.getClaimedWorlds()) {
//                for (VClaim vClaim : village.getClaims(world)) {
//                    addClaim(village, vClaim);
//                    claimsLoaded++;
//                }
//            }
        }

        logger(Level.INFO, "Loaded " + villagesLoaded + " villages and " + claimsLoaded + " claims.");

        int playersLoaded = 0;
        //Load players
        playerMap = new JPlayerMap(this);
        //Load Fundamentals players
        for (UUID uuid : getFundamentals().getPlayerMap().getKnownPlayers()) {
            playerMap.getPlayer(uuid);
            playersLoaded++;
        }

        logger(Level.INFO, "Loaded " + playersLoaded + " players from Fundamentals.");

        //Register commands
        this.getCommand("villageadmin").setExecutor(new JVilageAdminCMD(this));
        this.getCommand("village").setExecutor(new JVillageCMD(this));

        //Register events
        JVPlayerMoveListener playerMoveListener = new JVPlayerMoveListener(this);
        Bukkit.getPluginManager().registerEvents(playerMoveListener, plugin);

        JVPlayerAlterListener playerAlterListener = new JVPlayerAlterListener(this);
        Bukkit.getPluginManager().registerEvents(playerAlterListener, plugin);

        JVMobListener mobListener = new JVMobListener(this);
        Bukkit.getPluginManager().registerEvents(mobListener, plugin);

        //Scheduled Tasks

        //Save all villages every 5 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                plugin.logger(Level.INFO, "Saving all villages");
                villageMap.saveData();
            }
        }, 20 * 60 * 5, 20 * 60 * 5);
    }

    public int loadAllChunks(Village village) {
        int claimsLoaded = 0;
        for (String world : village.getClaimedWorlds()) {
            for (VClaim vClaim : village.getClaims(world)) {
                addClaim(village, vClaim);
                claimsLoaded++;
            }
        }
        return claimsLoaded;
    }

    public Fundamentals getFundamentals() {
        return (Fundamentals) Bukkit.getPluginManager().getPlugin("Fundamentals");
    }

    public JVillageMap getVillageMap() {
        return villageMap;
    }

    public JPlayerMap getPlayerMap() {
        return playerMap;
    }

    //Village at location
    public Village getVillageAtLocation(Location location) {
        VChunk vChunk = new VChunk(location);

        for (VClaim vClaim : claims) {
            if (vClaim.equals(vChunk)) {
                return getVillageMap().getVillage(vClaim.getVillage());
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (errored) {
            log.info("[" + pluginName + "] Has been disabled due to an error. No programmed shutdown procedures have been run.");
            return;
        }

        plugin.logger(Level.INFO, "Saving all villages");
        villageMap.saveData();

    }

    public void errorShutdown(String message) {
        log.info("[" + pluginName + "] Has Encountered An Error And Is Shutting Down");
        log.info("[" + pluginName + "] Error: " + message);
        errored = true;
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + plugin.getDescription().getName() + "] " + message);
    }

    public JVillageLanguage getLanguage() {
        return language;
    }

    public void deleteVillage(Village village) {
        //Remove all members
        ArrayList<UUID> membersToRemove = new ArrayList<>();

        for (UUID uuid : village.getMembers()) {
            membersToRemove.add(uuid);
        }

        //Remove all assistants
        for (UUID uuid : village.getAssistants()) {
            membersToRemove.add(uuid);
        }

        membersToRemove.add(village.getOwner());

        //Unselect village for owner
        VPlayer owner = playerMap.getPlayer(village.getOwner());
        if (owner.getSelectedVillage() == village) {
            owner.setSelectedVillage(null);
        }

        //Remove all claims from main plugin cache
//        for (String world : this.claims.keySet()) {
//            WorldClaimManager worldClaimManager = getWorldClaimManager(world, false);
//            worldClaimManager.removeClaimsByVillage(village);
//        }
        Iterator<VClaim> claimIterator = claims.iterator();
        while (claimIterator.hasNext()) {
            VClaim vClaim = claimIterator.next();
            if (vClaim.getVillage().equals(village)) {
                claimIterator.remove();
            }
        }

        //Remove village from vPlayers currently online
        for (Player player : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = playerMap.getPlayer(player.getUniqueId());
            if (vPlayer.getCurrentlyLocatedIn() == village) {
                vPlayer.setCurrentlyLocatedIn(null);
            }
        }

        //Remove membership of village from all members
        for (UUID uuid : membersToRemove) {
            VPlayer vPlayer = playerMap.getPlayer(uuid);
            vPlayer.removeVillageMembership(village);
        }

        //Remove from village map
        villageMap.deleteVillageFromExistence(village.getTownUUID());
    }

//    public void reRegisterVillage(Village village) {
//        for (String world : village.getClaimedWorlds()) {
//            for (VClaim vClaim : village.getClaims(world)) {
//                addClaim(village, vClaim);
//            }
//        }
//    }

    public boolean townyImport() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") == null || Bukkit.getServer().getPluginManager().getPlugin("Fundamentals") == null) {
            log.log(Level.WARNING, "[" + pluginName + "] Towny or Fundamentals not found, cancelling towny import.");
            return false;
        }

        Towny towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
        Fundamentals fundamentals = (Fundamentals) getServer().getPluginManager().getPlugin("Fundamentals");

        int townsImported = 0;
        int townsSkipped = 0;
        int residentsImported = 0;
        int residentsSkipped = 0;
        int claimsImported = 0;

        //Import towns
        for (Town town : towny.getTownyUniverse().getTowns()) {
            //Basic Town Info
            String originalTownName = town.getName();
            String newTownName = town.getName();

            int i = 0;
            while (!this.villageNameAvailable(newTownName)) {
                i++;
                newTownName = originalTownName + "-" + i;
            }

            //Print new Towny name if it got renamed
            if (i > 0) {
                log.log(Level.INFO, "Towny Import: " + originalTownName + " already exists, renaming to " + newTownName);
            }

            //Get Town UUID
            UUID townUUID = UUID.randomUUID();
            while (!this.villageUUIDAvailable(townUUID)) {
                townUUID = UUID.randomUUID();
            }


            UUID townOwnerUUID;
            ArrayList<UUID> assistants = new ArrayList<>();
            ArrayList<UUID> residents = new ArrayList<>();

            //Import Owner
            String townOwnerUsername = null;
            try {
                townOwnerUsername = town.getMayor().getName();
            } catch (Exception e) {
                logger(Level.WARNING, "A mayor was not found for town " + town.getName() + ", skipping town.");
                townsSkipped++;
                continue;
            }

            townOwnerUUID = fundamentals.getPlayerCache().getUUIDFromUsername(townOwnerUsername);
            if (townOwnerUUID == null) {
                townOwnerUUID = PoseidonUUID.getPlayerUUIDFromCache(townOwnerUsername, true);
            }
            if (townOwnerUUID == null) {
                townOwnerUUID = PoseidonUUID.getPlayerUUIDFromCache(townOwnerUsername, false);
            }
            if (townOwnerUUID == null) {
                log.warning("[" + pluginName + "] Could not find UUID for town owner: " + townOwnerUsername + ". The town will not be imported.");
                townsSkipped++;
                continue;
            }

            //Import Assistants
            for (Resident resident : town.getAssistants()) {
                String assistantUsername = resident.getName();
                UUID assistantUUID = fundamentals.getPlayerCache().getUUIDFromUsername(assistantUsername);
                if (assistantUUID == null) {
//                    log.warning("[" + pluginName + "] Could not find UUID for town assistant: " + assistantUsername + ". The assistant will not be imported for town " + newTownName + ".");
                    continue;
                }
                assistants.add(assistantUUID);
            }

            //Import Residents
            for (Resident resident : town.getResidents()) {
                String residentUsername = resident.getName();
                UUID residentUUID = fundamentals.getPlayerCache().getUUIDFromUsername(residentUsername);
                if (residentUUID == null) {
//                    log.warning("[" + pluginName + "] Could not find UUID for town resident: " + residentUsername + ". The resident will not be imported for town " + newTownName + ".");
                    residentsSkipped++;
                    continue;
                }
                residents.add(residentUUID);
                residentsImported++;
            }


            //Import Town Blocks (Claims)
            ArrayList<VChunk> townClaims = new ArrayList<>();
            //Loop through worlds to get claims
            for (TownyWorld townyWorld : towny.getTownyUniverse().getWorlds()) {
                for (TownBlock townBlock : townyWorld.getTownBlocks()) {
                    //Continue on if the town block is not claimed by a town
                    if (!townBlock.hasTown()) {
                        continue;
                    }
                    //Continue on if the town block is not claimed by the town we are importing
                    try {
                        //Don't continue if the town block is not claimed by the town we are importing
                        if (!townBlock.getTown().getName().equals(newTownName)) {
                            continue;
                        }
                        //Town block is claimed by the town we are importing
                        VChunk vChunk = new VChunk(townBlock.getWorld().getName(), townBlock.getX(), townBlock.getZ());
                        townClaims.add(vChunk);
                        claimsImported++;
                    } catch (Exception e) {
                        //Don't do anything if an exception is encountered
                        continue;
                    }
                }
            }

            //Misc
            VCords townSpawn = null;
            try {
                townSpawn = new VCords(town.getSpawn().getBlockX(), town.getSpawn().getBlockY(), town.getSpawn().getBlockZ(), town.getSpawn().getWorld().getName());
            } catch (Exception exception) {
                exception.printStackTrace();
                log.warning("[" + pluginName + "] Could not find town spawn for town " + newTownName + ". The town will not be imported.");
                townsSkipped++;
                continue;
            }

            //First chunk from townSpawn
            VChunk spawnChunk = new VChunk(townSpawn.getWorldName(), townSpawn.getX() >> 4, townSpawn.getZ() >> 4);


            //Create JVillage Town
            Village village = new Village(newTownName, townUUID, townOwnerUUID, spawnChunk, townSpawn);
            townsImported++;
            this.villageMap.addVillageToMap(village);
            //Register claims
            for (VChunk vClaim : townClaims) {
                village.addClaim(vClaim);
            }
            //Register assistants
            for (UUID assistantUUID : assistants) {
                village.addAssistant(assistantUUID);
            }
            //Register residents
            for (UUID residentUUID : residents) {
                village.addMember(residentUUID);
            }
            logger(Level.INFO, "Towny Import: Imported town " + newTownName + " with " + townClaims.size() + " claims. Owner Name: " + townOwnerUsername + " Assistants: " + assistants.size() + " Residents: " + residents.size());
        }

        //Import residents information

        int residentsImported2 = 0;
        int residentsSkipped2 = 0;
        for (int i = 0; towny.getTownyUniverse().getResidents().size() > i; i++) {
            Resident resident = towny.getTownyUniverse().getResidents().get(i);
            String residentUsername = resident.getName();
            UUID residentUUID = fundamentals.getPlayerCache().getUUIDFromUsername(residentUsername);
            if (residentUUID == null) {
//                log.warning("[" + pluginName + "] Could not find UUID for town resident: " + residentUsername + ". The resident will not be imported.");
                residentsSkipped2++;
                continue;
            }

            FundamentalsPlayer fundamentalsPlayer = fundamentals.getPlayerMap().getPlayer(residentUUID);

            // Save resident information
            fundamentalsPlayer.saveInformation("jvillage.firstjoin", resident.getRegistered());
            fundamentalsPlayer.saveInformation("jvillage.lastjoin", resident.getLastOnline());
            residentsImported2++;
        }

        //Import information
        logger(Level.INFO, "Towny Import: Imported " + townsImported + " towns and skipped " + townsSkipped + " towns.");
        logger(Level.INFO, "Towny Import: Imported " + residentsImported + " town residents and skipped " + residentsSkipped + " town residents.");
        logger(Level.INFO, "Towny Import: Imported " + claimsImported + " claims.");

        logger(Level.INFO, "Resident Import: Imported " + residentsImported2 + " residents and skipped " + residentsSkipped2 + " residents.");

        return true;

    }


//    private WorldClaimManager getWorldClaimManager(String world, boolean generate) {
//        world = world.toLowerCase();
//
//        if (claims.containsKey(world)) {
//            return claims.get(world);
//        }
//        if (generate) {
//            logger(Level.INFO, "Generating new WorldClaimManager for world " + world);
//            WorldClaimManager worldClaimManager = new WorldClaimManager(plugin, world);
//            claims.put(world, worldClaimManager);
//            return worldClaimManager;
//        }
//        return null;
//    }


    public Village generateNewVillage(String townName, UUID owner, VChunk vChunk, VCords townSpawn) {
        if (!villageNameAvailable(townName)) {
            return null;
        }

        UUID uuid = UUID.randomUUID();
        while (!villageUUIDAvailable(uuid)) {
            uuid = UUID.randomUUID();
        }

        return new Village(townName, uuid, owner, vChunk, townSpawn);

    }

    public boolean villageNameAvailable(String villageName) {
        //TODO: Implement a cache so this doesn't have to loop through every village file
        for (UUID villageUUID : villageMap.getKnownVillages()) {
            Village village = villageMap.getVillage(villageUUID);
            if (village.getTownName().equalsIgnoreCase(villageName)) {
                return false;
            }
        }
        return true;
    }

    public boolean villageUUIDAvailable(UUID uuid) {
        if (villageMap.getVillage(uuid) != null) {
            return false;
        }
        return true;
    }


    public boolean addClaim(Village village, VChunk vChunk) {
//        WorldClaimManager worldClaimManager = getWorldClaimManager(vChunk.getWorldName(), false);
//        return worldClaimManager.addClaim(village, vChunk);
        if(vChunk instanceof VClaim) {
            return claims.add((VClaim) vChunk);
        }
        return claims.add(new VClaim(village, vChunk));
    }

    public boolean removeClaim(VChunk vChunk) {
        return claims.remove(vChunk);
//        WorldClaimManager worldClaimManager = getWorldClaimManager(vChunk.getWorldName(), false);
//        return worldClaimManager.removeClaim(vChunk);
    }

    public boolean isClaimed(VChunk vChunk) {
//        WorldClaimManager worldClaimManager = getWorldClaimManager(vChunk.getWorldName(), false);
//        return worldClaimManager.isClaimed(vChunk);
        return claims.contains(vChunk);
    }
}
