package com.johnymuffin.jvillage.beta;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.webapi.JWebAPI;
import com.johnymuffin.beta.webapi.event.JWebAPIDisable;
import com.johnymuffin.jvillage.beta.commands.JVilageAdminCMD;
import com.johnymuffin.jvillage.beta.commands.JVillageCMD;
import com.johnymuffin.jvillage.beta.commands.VResidentCommand;
import com.johnymuffin.jvillage.beta.config.JPlayerData;
import com.johnymuffin.jvillage.beta.config.JVillageLanguage;
import com.johnymuffin.jvillage.beta.config.JVillageSettings;
import com.johnymuffin.jvillage.beta.interfaces.ClaimManager;
import com.johnymuffin.jvillage.beta.listeners.JVMobListener;
import com.johnymuffin.jvillage.beta.listeners.JVPlayerAlterListener;
import com.johnymuffin.jvillage.beta.listeners.JVPlayerMoveListener;
import com.johnymuffin.jvillage.beta.maps.JPlayerMap;
import com.johnymuffin.jvillage.beta.maps.JVillageMap;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import com.johnymuffin.jvillage.beta.routes.api.v1.JVillageGetPlayerRoute;
import com.johnymuffin.jvillage.beta.routes.api.v1.JVillageGetVillageList;
import com.johnymuffin.jvillage.beta.routes.api.v1.JVillageGetVillageRoute;
import com.johnymuffin.jvillage.beta.tasks.AutoClaimingTask;
import com.johnymuffin.jvillage.beta.tasks.AutoUnclaimingTask;
import com.johnymuffin.jvillage.beta.tasks.AutomaticSaving;
import com.johnymuffin.jvillage.beta.tasks.Metrics;
import com.legacyminecraft.poseidon.event.PoseidonCustomListener;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Role;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.johnymuffin.UUIDManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.johnymuffin.jvillage.beta.JVUtility.getUUIDFromPoseidonCache;

public class JVillage extends JavaPlugin implements ClaimManager, PoseidonCustomListener {
    //Basic Plugin Info
    private static JVillage plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //    private HashMap<VillageEntry, Village> villages = new HashMap<>(); //Main list for all villages
//    private HashMap<String, WorldClaimManager> claims = new HashMap<>();
//    private ArrayList<VClaim> claims = new ArrayList<>();
    private HashMap<Village, ArrayList<VClaim>> claims = new HashMap<>();

    private JVillageLanguage language;
    private JVillageSettings settings;

    private JPlayerData playerData;

    private boolean errored = false;

    private JVillageMap villageMap;
    private JPlayerMap playerMap;

    private boolean apiEnabled = false;

    private Metrics metrics;

    private boolean fundamentalsEnabled = false;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        //Check for Fundamentals
        if (Bukkit.getPluginManager().getPlugin("Fundamentals") == null) {
            fundamentalsEnabled = false;
            log.warning("[" + pluginName + "] Fundamentals is not installed or not enabled, economy features will be disabled");
        } else {
            log.info("[" + pluginName + "] Fundamentals is installed and enabled, economy features will be enabled");
            fundamentalsEnabled = true;
        }


        //Config files
        settings = new JVillageSettings(new File(this.getDataFolder(), "settings.yml"));
        debugMode = settings.getConfigBoolean("settings.debug-mode.enabled"); //Set debug mode from config
        language = new JVillageLanguage(new File(this.getDataFolder(), "language.yml"), settings.getConfigBoolean("settings.always-use-default-lang.enabled"));

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
            int claims = village.getTotalClaims();
            claimsLoaded += claims;
        }

        logger(Level.INFO, "Loaded " + villagesLoaded + " villages and " + claimsLoaded + " claims.");


//        logger(Level.INFO, "Checking for duplicate claims...");
//        int duplicateClaims = 0;
//
//
//        long startTime = System.currentTimeMillis();
//        for (VClaim vClaim : this.getAllClaims()) {
//            Village[] villagesOnClaim = this.getVillagesAtLocation(vClaim);
//            if (villagesOnClaim.length > 1) {
//                duplicateClaims++;
//                logger(Level.WARNING, "Duplicate claim found at " + vClaim.toString() + " for villages " + formatVillageList(villagesOnClaim) + ". It is advised that you delete this claim from the JSON file or unclaim it with \"/va village unclaim\" while standing in it.");
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        logger(Level.INFO, "Checked " + claimsLoaded + " claims in " + (endTime - startTime) + "ms. Found " + duplicateClaims + " duplicate claims.");

        this.playerData = new JPlayerData(this); // Load player data from file

        int playersLoaded = 0;
        //Load players
        playerMap = new JPlayerMap(this);
        //TODO: This really isn't needed anymore, but I'm keeping it here for now. Lazy loading is a thing now.
        for(UUID uuid : playerData.getAllPlayers()) {
            playerMap.getPlayer(uuid);
            playersLoaded++;
        }

        //Load Fundamentals players
        //for (UUID uuid : getFundamentals().getPlayerMap().getKnownPlayers()) {
        //    playerMap.getPlayer(uuid);
         //   playersLoaded++;
        //}

        logger(Level.INFO, "Loaded " + playersLoaded + " players from player data file.");

        //Register commands
        this.getCommand("villageadmin").setExecutor(new JVilageAdminCMD(this));
        this.getCommand("village").setExecutor(new JVillageCMD(this));
        this.getCommand("member").setExecutor(new VResidentCommand(this));

        //Register events
        JVPlayerMoveListener playerMoveListener = new JVPlayerMoveListener(this);
        Bukkit.getPluginManager().registerEvents(playerMoveListener, plugin);

        JVPlayerAlterListener playerAlterListener = new JVPlayerAlterListener(this);
        Bukkit.getPluginManager().registerEvents(playerAlterListener, plugin);

        JVMobListener mobListener = new JVMobListener(this);
        Bukkit.getPluginManager().registerEvents(mobListener, plugin);

        //Register API routes if JWebAPI is installed
        if (Bukkit.getPluginManager().getPlugin("JWebAPI") != null) {
            logger(Level.INFO, "JWebAPI found, registering API routes");
            JWebAPI jWebAPI = (JWebAPI) Bukkit.getPluginManager().getPlugin("JWebAPI");
            jWebAPI.registerRoute(JVillageGetPlayerRoute.class, "/api/v1/village/getPlayer");
            jWebAPI.registerRoute(JVillageGetVillageRoute.class, "/api/v1/village/getVillage");
            jWebAPI.registerRoute(JVillageGetVillageList.class, "/api/v1/village/getVillageList");
            apiEnabled = true;
            logger(Level.INFO, "API routes registered");
        }

        Bukkit.getPluginManager().registerEvents(this, this);


//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 500; i++) {
//            //Random X and Z within 30000 blocks of 0,0
//            int x = (int) (Math.random() * 60000) - 30000;
//            int z = (int) (Math.random() * 60000) - 30000;
//
//            //Get closest village
//            VClaim claim = findClosestClaim(new VCords(x, 0, z, Bukkit.getWorlds().get(0).getName()));
//            Village village = getVillageMap().getVillage(claim.getVillage());
//            logger(Level.INFO, "Closest claim to " + x + ", " + z + " is " + village.getTownName() + " at " + (claim.getX() * 16) + ", " + (claim.getZ() * 16));
//        }
//        long endTime = System.currentTimeMillis();
//        logger(Level.INFO, "Took " + (endTime - startTime) + "ms to find 100 closest claims");

        //Run auto claim task
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new AutoClaimingTask(plugin), 1, this.getSettings().getConfigInteger("settings.auto-claim.timer") * 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new AutoUnclaimingTask(plugin), 1, this.getSettings().getConfigInteger("settings.auto-claim.timer") * 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new AutomaticSaving(plugin), 1, 20 * 60 * 10); //Save every 10 minutes

        //bstats metrics
        int pluginId = 20618; //JVillage plugin ID
        this.metrics = new Metrics(plugin, pluginId);

    }

    public void removeAPIRoutes() {
        if (apiEnabled) {
            logger(Level.INFO, "Unregistering API routes");
            JWebAPI jWebAPI = (JWebAPI) Bukkit.getPluginManager().getPlugin("JWebAPI");
            try {
                jWebAPI.unregisterServlets(JVillageGetPlayerRoute.class);
                jWebAPI.unregisterServlets(JVillageGetVillageRoute.class);
                jWebAPI.unregisterServlets(JVillageGetVillageList.class);
                logger(Level.INFO, "Unregistered API routes");
            } catch (Exception e) {
                logger(Level.WARNING, "Failed to unregister API routes");
                e.printStackTrace();
            }

        }
    }

    @EventHandler
    public void onCustomEvent(final Event customEvent) {
        if (!apiEnabled) return;
        if (!(customEvent instanceof JWebAPIDisable)) return;
        //Remove API routes if enabled and JStoreDisableEvent is called
        logger(Level.INFO, "JWebAPI disabled, removing API routes");
        removeAPIRoutes();
    }

    @Override
    public void onDisable() {
        logger(Level.INFO, "Saving all villages");
        villageMap.saveData();
        playerData.save();

        metrics.shutdown(); //Disable bstats metrics incase a reload is done
        removeAPIRoutes();

        if (errored) {
            logger(Level.INFO, "Has been disabled due to an error. No programmed shutdown procedures have been run.");
            return;
        }
        logger(Level.INFO, "Has been disabled.");
    }

//    public int loadAllChunks(Village village) {
//        int claimsLoaded = 0;
//        for (String world : village.getWorldsWithClaims()) {
//            for (VClaim vClaim : village.getClaims(world)) {
//                addClaim(vClaim);
//                claimsLoaded++;
//            }
//        }
//        return claimsLoaded;
//    }

//    public void exportOverviewerRegions() throws IOException {
//        logger(Level.INFO, "Exporting Overviewer Regions");
//        File regionFolder = new File(this.getDataFolder(), "regions");
//        if (!regionFolder.exists()) {
//            regionFolder.mkdirs();
//        }
//        OverviewerExporter exporter = new OverviewerExporter(this, regionFolder);
//        exporter.generateOverviewerRegions();
//        logger(Level.INFO, "Exported Overviewer Regions");
//    }

    public JVillageMap getVillageMap() {
        return villageMap;
    }

    public JPlayerMap getPlayerMap() {
        return playerMap;
    }

    //Village at location
    public Village getVillageAtLocation(Location location) {
        VChunk vChunk = new VChunk(location);

        for (VClaim vClaim : getAllClaims()) {
            if (vClaim.equals(vChunk)) {
                return getVillageMap().getVillage(vClaim.getVillage());
            }
        }
        return null;
    }

    public Village getVillageAtLocation(VChunk vChunk) {
        for (VClaim vClaim : getAllClaims()) {
            if (vClaim.equals(vChunk)) {
                return getVillageMap().getVillage(vClaim.getVillage());
            }
        }
        return null;
    }

    private Village[] getVillagesAtLocation(VChunk vChunk) {
        ArrayList<Village> villages = new ArrayList<>();
        for (VClaim vClaim : getAllClaims()) {
            if (vClaim.equals(vChunk)) {
                villages.add(getVillageMap().getVillage(vClaim.getVillage()));
            }
        }
        //Remove duplicate entries
        return villages.stream().distinct().toArray(Village[]::new);

    }

    public ArrayList<VClaim> getAllClaims() {
        ArrayList<VClaim> allClaims = new ArrayList<>();
        for (ArrayList<VClaim> vClaims : claims.values()) {
            allClaims.addAll(vClaims);
        }
        return allClaims;
    }

    public void errorShutdown(String message) {
        log.info("[" + pluginName + "] Has Encountered An Error And Is Shutting Down");
        log.info("[" + pluginName + "] Error: " + message);
        errored = true;
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public boolean worldGuardIsClaimAllowed(VCords vCords) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return true; //Assume yes if worldguard is disabled
        }

        if (!this.getSettings().getConfigBoolean("settings.world-guard.blocked-regions.enabled")) {
            return true;
        }

        try {
            WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            RegionManager regionManager = worldGuardPlugin.getRegionManager(Bukkit.getWorld(vCords.getWorldName()));
            ApplicableRegionSet regionSet = regionManager.getApplicableRegions(vCords.getLocation());
            List<String> blockedRegions = settings.getWorldGuardPermissions();

            for (ProtectedRegion protectedRegion : regionSet) {
                for (String blockedRegion : blockedRegions) {
                    if (protectedRegion.getId().equalsIgnoreCase(blockedRegion)) {
                        return false;
                    }
                }
            }

            return true; //Claim is allowed
        } catch (Exception e) {
            logger(Level.WARNING, "Error checking WorldGuard region to see if a claim is allowed. Are you running a compatible WorldGuard version? JVillage will allow the claim as a result of this error.");
            e.printStackTrace();
            return true; //Assume yes if worldguard is disabled
        }
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + plugin.getDescription().getName() + "] " + message);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    private boolean debugMode = false;
    public void debugLogger(Level level, String message) {
        if (debugMode) {
            Bukkit.getLogger().log(level, "[" + plugin.getDescription().getName() + "] [DEBUG] " + message);
        }
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
        if (this.claims.remove(village) == null) {
            logger(Level.WARNING, "Failed to remove village from claims cache.");
            throw new RuntimeException("Failed to remove village from claims cache.");
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

//    public boolean townyImportHomeChunks() {
//        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") == null || Bukkit.getServer().getPluginManager().getPlugin("Fundamentals") == null) {
//            log.log(Level.WARNING, "[" + pluginName + "] Towny or Fundamentals not found, cancelling towny import.");
//            return false;
//        }
//
//        Towny towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
//        Fundamentals fundamentals = (Fundamentals) getServer().getPluginManager().getPlugin("Fundamentals");
//
//
//
//    }

    public boolean townyImport() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") == null) {
            log.log(Level.WARNING, "[" + pluginName + "] Towny not found, cancelling towny import.");
            return false;
        }

        UUID placeholderUUID = UUID.fromString(settings.getConfigString("settings.import-placeholder-account.uuid"));
        String placeholderUsername = settings.getConfigString("settings.import-placeholder-account.name");
        //Add UUID to Poseidon UUIDManager
        try {
            UUIDManager.getInstance().receivedUUID(placeholderUsername, placeholderUUID, (System.currentTimeMillis() / 1000L), true);
        } catch (Exception exception) {
            logger(Level.WARNING, "Could not add placeholder UUID to Poseidon UUIDCache. This could cause Unknown User to be shown for some imported Villages.");
        }

        Towny towny = (Towny) getServer().getPluginManager().getPlugin("Towny");

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

            townOwnerUUID = this.getUUIDFromUsername(townOwnerUsername);
            if (townOwnerUUID == null) {
                townOwnerUUID = PoseidonUUID.getPlayerUUIDFromCache(townOwnerUsername, true);
            }
            if (townOwnerUUID == null) {
                townOwnerUUID = PoseidonUUID.getPlayerUUIDFromCache(townOwnerUsername, false);
            }
            if (townOwnerUUID == null) {
                log.warning("[" + pluginName + "] Could not find UUID for town owner: " + townOwnerUsername + ". The town will not be imported.");
                townsSkipped++;
                townOwnerUUID = placeholderUUID; //UUID for jetpackingwolf
            }

            //Import Assistants
            for (Resident resident : town.getAssistants()) {
                String assistantUsername = resident.getName();
                UUID assistantUUID = this.getUUIDFromUsername(assistantUsername);
                if (assistantUUID == null) {
                    log.warning("[" + pluginName + "] Could not find UUID for town assistant: " + assistantUsername + ". The assistant will not be imported for town " + newTownName + ".");
                    continue;
                }
                assistants.add(assistantUUID);
            }

            //Import Residents
            for (Resident resident : town.getResidents()) {
                String residentUsername = resident.getName();
                UUID residentUUID = this.getUUIDFromUsername(residentUsername);
                if (residentUUID == null) {
                    log.warning("[" + pluginName + "] Could not find UUID for town resident: " + residentUsername + ". The resident will not be imported for town " + newTownName + ".");
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
                log.warning("[" + pluginName + "] Could not find town spawn for town " + newTownName + ". World spawn will be used instead.");
                logger(Level.WARNING, "Could not find town spawn for town " + newTownName + ". The town spawn will be set to the world spawn.");
                townSpawn = new VCords(Bukkit.getWorlds().get(0).getSpawnLocation().getBlockX(), Bukkit.getWorlds().get(0).getSpawnLocation().getBlockY(), Bukkit.getWorlds().get(0).getSpawnLocation().getBlockZ(), Bukkit.getWorlds().get(0).getName());
            }

            //First chunk from townSpawn
            VChunk spawnChunk = new VChunk(townSpawn.getWorldName(), townSpawn.getX() >> 4, townSpawn.getZ() >> 4);


            //Create JVillage Town
            Village village = new Village(plugin, newTownName, townUUID, townOwnerUUID, spawnChunk, townSpawn);
            townsImported++;
            this.villageMap.addVillageToMap(village);
            //Register claims
            for (VChunk vClaim : townClaims) {
                village.addClaim(new VClaim(village, vClaim));
//                village.addClaim(vClaim);
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
            UUID residentUUID = this.getUUIDFromUsername(residentUsername);
            if (residentUUID == null) {
//                log.warning("[" + pluginName + "] Could not find UUID for town resident: " + residentUsername + ". The resident will not be imported.");
                residentsSkipped2++;
                continue;
            }

            // Save resident information
            playerData.setPlayerData(residentUUID, "firstJoin", String.valueOf(resident.getRegistered()/1000L));
            playerData.setPlayerData(residentUUID, "lastJoin", String.valueOf(resident.getLastOnline()/1000L));
            residentsImported2++;
        }

        //Import information
        logger(Level.INFO, "Towny Import: Imported " + townsImported + " towns and skipped " + townsSkipped + " towns.");
        logger(Level.INFO, "Towny Import: Imported " + residentsImported + " town residents and skipped " + residentsSkipped + " town residents.");
        logger(Level.INFO, "Towny Import: Imported " + claimsImported + " claims.");

        logger(Level.INFO, "Resident Import: Imported " + residentsImported2 + " residents and skipped " + residentsSkipped2 + " residents.");

        return true;

    }

    public boolean factionsImport() {
        logger(Level.INFO, "Factions Import: Starting import of Factions data.");

        UUID placeholderUUID = UUID.fromString(settings.getConfigString("settings.import-placeholder-account.uuid"));
        String placeholderUsername = settings.getConfigString("settings.import-placeholder-account.name");
        //Add UUID to Poseidon UUIDManager
        try {
            UUIDManager.getInstance().receivedUUID(placeholderUsername, placeholderUUID, (System.currentTimeMillis() / 1000L), true);
        } catch (Exception exception) {
            logger(Level.WARNING, "Could not add placeholder UUID to Poseidon UUIDCache. This could cause Unknown User to be shown for some imported Villages.");
        }

        //Get Private Factions Board
        HashMap<FLocation, Integer> factionClaims;
        try {
            Field flocationIdsField = Board.class.getDeclaredField("flocationIds");
            flocationIdsField.setAccessible(true);
            factionClaims = (HashMap<FLocation, Integer>) flocationIdsField.get(null);
        } catch (Exception exception) {
            logger(Level.WARNING, "Could not get Factions board. Factions import will be skipped. Please note Factions import is only supported on Java 8 due to the use of reflections.");
            return false;
        }

        if(factionClaims == null) {
            logger(Level.WARNING, "Could not get Factions board. Factions import will be skipped.");
            return false;
        }



        if (Bukkit.getServer().getPluginManager().getPlugin("Factions") == null) {
            log.log(Level.WARNING, "[" + pluginName + "] Factions not found, cancelling towny import.");
            return false;
        }

        Factions factions = (Factions) Bukkit.getServer().getPluginManager().getPlugin("Factions");

        int factionsImported = 0;
        int factionsSkipped = 0;
        int membersImported = 0;
        int membersSkipped = 0;
        int claimsImported = 0;


        Set<String> factionTags = factions.getFactionTags();

        for (String tag : factionTags) {
            Faction faction = Faction.findByTag(tag);
            String originalFactionName = faction.getTag();
            String newVillageName = originalFactionName;

            int i = 0;
            while (!this.villageNameAvailable(newVillageName)) {
                i++;
                newVillageName = originalFactionName + "-" + i;
            }

            //Print out the new name if it was changed
            if (!originalFactionName.equals(newVillageName)) {
                logger(Level.INFO, "Faction Import: Village " + originalFactionName + " already exists. Changing name to " + newVillageName);
            }

            //Get Village UUID
            UUID factionUUID = UUID.randomUUID();


            UUID villageOwnerUUID = null;
            ArrayList<UUID> assistants = new ArrayList<>();
            ArrayList<UUID> members = new ArrayList<>();

            Set<String> factionMembers = factions.getPlayersInFaction(tag);


            String ownerUsername = null;
            ArrayList<String> assistantUsernames = new ArrayList<>();
            ArrayList<String> memberUsernames = new ArrayList<>();

            //Find owner
            for (String member : factionMembers) {
                FPlayer fPlayer = FPlayer.find(member);
                if (fPlayer.getRole() == Role.ADMIN) {
                    ownerUsername = fPlayer.getName();
                } else if (fPlayer.getRole() == Role.MODERATOR) {
                    assistantUsernames.add(fPlayer.getName());
                } else {
                    memberUsernames.add(fPlayer.getName());
                }
            }

            //Skip if owner is null
            if (ownerUsername == null) {
                log.warning("[" + pluginName + "] Could not find owner for faction " + newVillageName + ". The faction will not be imported.");
                factionsSkipped++;
                continue;
            }

            villageOwnerUUID = this.getUUIDFromUsername(ownerUsername);

            if (villageOwnerUUID == null) {
                log.warning("[" + pluginName + "] Could not find UUID for faction (" + newVillageName + ") owner: " + ownerUsername + ". Ownership will be given to " + placeholderUsername + " (" + placeholderUUID + ").");
                villageOwnerUUID = placeholderUUID;
            }

            //Import Assistants
            for (String assistantUsername : assistantUsernames) {
                UUID assistantUUID = this.getUUIDFromUsername(assistantUsername);
                if (assistantUUID == null) {
                    log.warning("[" + pluginName + "] Could not find UUID for faction (" + newVillageName + ") assistant: " + assistantUsername + ". The assistant will not be imported.");
                    continue;
                }
                assistants.add(assistantUUID);
            }

            //Import Members
            for (String memberUsername : memberUsernames) {
                UUID memberUUID = this.getUUIDFromUsername(memberUsername);
                if (memberUUID == null) {
                    log.warning("[" + pluginName + "] Could not find UUID for faction (" + newVillageName + ") member: " + memberUsername + ". The member will not be imported.");
                    continue;
                }
                members.add(memberUUID);
                membersImported++;
            }


            //Import Claims
            ArrayList<VChunk> villageClaims = new ArrayList<>();


            int factionId = faction.getId();

            //Loop through factionClaims
            for (Map.Entry<FLocation, Integer> entry : factionClaims.entrySet()) {
                FLocation fLocation = entry.getKey();
                int id = entry.getValue();
                if(id == factionId) {
                    VChunk vChunk = new VChunk(fLocation.getWorldName(), (int) fLocation.getX(), (int) fLocation.getZ());
                    villageClaims.add(vChunk);
                    claimsImported++;
                }
            }

            //Skip if no claims
            if (villageClaims.size() == 0) {
                log.warning("[" + pluginName + "] Faction " + newVillageName + " has no claims. The faction will not be imported.");
                factionsSkipped++;
                continue;
            }


            //Faction Spawn
            Location factionSpawn = faction.getHome();
            VCords villageSpawn = null;
            VChunk spawnChunk = null;
            if(factionSpawn == null) {
                log.warning("[" + pluginName + "] Faction " + newVillageName + " has no spawn. The world spawn will be used instead.");
                spawnChunk = new VChunk(Bukkit.getWorlds().get(0).getSpawnLocation());
                villageSpawn = new VCords(Bukkit.getWorlds().get(0).getSpawnLocation());
            } else {
                spawnChunk = new VChunk(factionSpawn);
                villageSpawn = new VCords(factionSpawn);
            }


            //Create Village
            Village village = new Village(plugin, newVillageName, factionUUID, villageOwnerUUID, spawnChunk, villageSpawn);
            factionsImported++;
            this.villageMap.addVillageToMap(village);
            //Register claims
            for (VChunk vChunk : villageClaims) {
                village.addClaim(new VClaim(village, vChunk));
            }
            //Register assistants
            for (UUID assistantUUID : assistants) {
                village.addAssistant(assistantUUID);
            }
            //Register members
            for (UUID memberUUID : members) {
                village.addMember(memberUUID);
            }

            logger(Level.INFO, "Faction Import: Imported faction " + newVillageName + " with " + villageClaims.size() + " claims, " + assistants.size() + " assistants, and " + members.size() + " members.");
        }

        logger(Level.INFO, "Faction Import: Imported " + factionsImported + " factions with " + claimsImported + " claims and " + membersImported + " members. Skipped " + factionsSkipped + " factions.");


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


    public VClaim findClosestClaim(VCords cords) {
        VClaim closestClaim = null;
        double closestDistance = Double.MAX_VALUE;
        for (VClaim claim : plugin.getClaimsInWorld(cords.getWorldName())) {
            int claimX = claim.getX() * 16 + 8;
            int claimZ = claim.getZ() * 16 + 8;
            double distance = Math.sqrt(Math.pow(claimX - cords.getX(), 2) + Math.pow(claimZ - cords.getY(), 2));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestClaim = claim;
            }
        }
        return closestClaim;
    }

    public VClaim[] getClaimsInRadius(VCords cords, double radius) {
        ArrayList<VClaim> claimsInRadius = new ArrayList<>();
        for (VClaim claim : plugin.getClaimsInWorld(cords.getWorldName())) {
            int claimX = claim.getX() * 16 + 8;
            int claimZ = claim.getZ() * 16 + 8;
            double distance = Math.sqrt(Math.pow(claimX - cords.getX(), 2) + Math.pow(claimZ - cords.getY(), 2));
            if (distance < radius) {
                claimsInRadius.add(claim);
            }
        }
        return claimsInRadius.toArray(new VClaim[0]);
    }

    public VClaim[] getClaimsInWorld(String worldName) {
        ArrayList<VClaim> claimsInWorld = new ArrayList<>();
        for (VClaim claim2 : getAllClaims()) {
            if (claim2.getWorldName().equals(worldName)) {
                claimsInWorld.add(claim2);
            }
        }
        return claimsInWorld.toArray(new VClaim[0]);
    }

    public JVillageSettings getSettings() {
        return settings;
    }

    public JPlayerData getPlayerData() {
        return this.playerData;
    }

    public Village generateNewVillage(String townName, UUID owner, VChunk vChunk, VCords townSpawn) {
        if (!villageNameAvailable(townName)) {
            return null;
        }

        UUID uuid = UUID.randomUUID();
        while (!villageUUIDAvailable(uuid)) {
            uuid = UUID.randomUUID();
        }

        return new Village(plugin, townName, uuid, owner, vChunk, townSpawn);

    }

    public ArrayList<VClaim> getVillageClaimsArray(Village village) {
        if (!this.claims.containsKey(village)) {
            this.claims.put(village, new ArrayList<>());
        }

        return this.claims.get(village);
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


    public boolean addClaim(VClaim vChunk) {
//        WorldClaimManager worldClaimManager = getWorldClaimManager(vChunk.getWorldName(), false);
//        return worldClaimManager.addClaim(village, vChunk);
        Village village = getVillageMap().getVillage(vChunk.getVillage());
        return this.claims.get(village).add(vChunk);
    }

    public boolean removeClaim(VClaim vChunk) {
        Village village = getVillageMap().getVillage(vChunk.getVillage());

        ArrayList<VClaim> villageClaims = this.claims.get(village);
        return villageClaims.remove(vChunk);
    }

    public boolean isClaimed(VChunk vChunk) {
        ArrayList<VClaim> villageClaims = getAllClaims();
        for (VClaim vClaim : villageClaims) {
            if (vClaim.equals(vChunk)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public UUID getUUIDFromUsername(String username) {
        UUID uuid = getUUIDFromPoseidonCache(username);
        if (uuid == null) {
            uuid = playerData.getUUID(username);
        }

        //Check if Fundamentals is enabled and if so, use it's cache
        if(uuid == null && fundamentalsEnabled && Bukkit.getPluginManager().isPluginEnabled("Fundamentals")) {
            Fundamentals fundamentals = (Fundamentals) Bukkit.getPluginManager().getPlugin("Fundamentals");
            uuid = fundamentals.getPlayerCache().getUUIDFromUsername(username);
        }

        return uuid;
    }

    @Nullable
    public String getUsernameFromUUID(UUID uuid) {
        String username = PoseidonUUID.getPlayerUsernameFromUUID(uuid);

        if (username == null) {
            username = playerData.getUsername(uuid);
        }

        //Check if Fundamentals is enabled and if so, use it's cache
        if(uuid == null && fundamentalsEnabled && Bukkit.getPluginManager().isPluginEnabled("Fundamentals")) {
            Fundamentals fundamentals = (Fundamentals) Bukkit.getPluginManager().getPlugin("Fundamentals");
            username = fundamentals.getPlayerCache().getUsernameFromUUID(uuid);
        }

        return username;
    }

    public boolean isFundamentalsEnabled() {
        return fundamentalsEnabled;
    }
}
