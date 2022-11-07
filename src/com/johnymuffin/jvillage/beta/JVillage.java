package com.johnymuffin.jvillage.beta;

import com.johnymuffin.jvillage.beta.models.VChunk;
import com.johnymuffin.jvillage.beta.models.VCords;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageEntry;
import com.johnymuffin.jvillage.beta.world.VWorldClaims;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class JVillage extends JavaPlugin {
    //Basic Plugin Info
    private static JVillage plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private HashMap<VillageEntry, Village> villages = new HashMap<>(); //Main list for all villages
    private HashMap<String, VWorldClaims> claims = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

    }

    @Override
    public void onDisable() {

    }


    public VWorldClaims getVWorldClaims(String world, boolean generate) {
        if (claims.containsKey(world)) {
            return claims.get(world);
        }
        if (generate) {
            VWorldClaims vWorldClaims = new VWorldClaims(plugin, world);
            claims.put(world, vWorldClaims);
            return vWorldClaims;
        }
        return null;
    }


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
        for (VillageEntry villageEntry : villages.keySet()) {
            if (villageEntry.getVillageName().equalsIgnoreCase(villageName)) {
                return false;
            }
        }
        return true;
    }

    public boolean villageUUIDAvailable(UUID uuid) {
        for (VillageEntry villageEntry : villages.keySet()) {
            if (villageEntry.getUUID().equals(uuid)) {
                return false;
            }
        }
        return true;
    }


}
