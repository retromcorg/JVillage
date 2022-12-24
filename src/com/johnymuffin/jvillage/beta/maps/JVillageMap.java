package com.johnymuffin.jvillage.beta.maps;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class JVillageMap {
    private JVillage plugin;

    private HashMap<UUID, Village> villageMap = new HashMap<>();
    private ArrayList<UUID> knownVillages = new ArrayList<>();

    private boolean cacheAllVillages = true;
    private int villagesLoaded;

    public JVillageMap(JVillage plugin) {
        this.plugin = plugin;


        //Load Known List
        Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        File dataFolder = new File(plugin.getDataFolder(), "villages");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        //Loop through
        for (String file : dataFolder.list()) {
            if (!file.endsWith(".json")) {
                continue;
            }
            String sanitizedUUID = file.replaceAll(".json", "");
            if (!p.matcher(sanitizedUUID).matches()) {
                plugin.logger(Level.WARNING, "Corrupt UUID Found: " + sanitizedUUID + " - " + file);
                continue;
            }
            UUID villageUUID = UUID.fromString(sanitizedUUID);
            knownVillages.add(villageUUID);
            if (cacheAllVillages) {
                try {
                    //Add village data to cache if option is enabled
                    getVillage(villageUUID);
                    villagesLoaded = villagesLoaded + 1;
                } catch (Exception exception) {
                    knownVillages.remove(villageUUID);
                    plugin.logger(Level.WARNING, "Error loading village data for " + villageUUID + "into cache.");
                    removeVillageFromMap(villageUUID);
                }
            }
        }
    }

    public UUID[] getKnownVillages() {
        return knownVillages.toArray(new UUID[knownVillages.size()]);
    }

    private void removeVillageFromMap(UUID villageUUID) {
        villageMap.remove(villageUUID);
    }

    public void deleteVillageFromExistence(UUID villageUUID) {
        villageMap.remove(villageUUID);
        knownVillages.remove(villageUUID);

        File villageDataFile = new File(plugin.getDataFolder(), "villages" + File.separator + villageUUID + ".json");
        plugin.logger(Level.INFO, "Removing village data file for " + villageUUID + " from disk as it has been deleted.");
        if (villageDataFile.exists()) {
            villageDataFile.delete();
        }
    }

    public boolean isVillageKnown(UUID villageUUID) {
        if (villageUUID == null) {
            return false;
        }

        return knownVillages.contains(villageUUID);
    }

    public Village getVillage(String name) {
        for (UUID villageUUID : knownVillages) {
            Village village = getVillage(villageUUID);
            if (village.getTownName().equalsIgnoreCase(name)) {
                return village;
            }
        }
        return null;
    }

    public Village getVillage(UUID villageUUID) {

        if (!isVillageKnown(villageUUID)) {
            return null;
        }

        if (villageMap.containsKey(villageUUID)) {
            return villageMap.get(villageUUID);
        }

        //Load Village
        try {
            Village village = loadVillage(villageUUID);
            villageMap.put(villageUUID, village);
            return village;
        } catch (Exception exception) {
            plugin.logger(Level.WARNING, "Error loading village data for " + villageUUID + " into cache.");
            exception.printStackTrace();
            plugin.errorShutdown("Error loading village data for " + villageUUID + " into cache.");
            return null;
        }
    }

    public void saveData() {
        for (Village village : villageMap.values()) {
            if (village.isModified()) {
                saveVillage(village);
            }
        }
    }

    public void addVillageToMap(Village village) {
        if (knownVillages.contains(village.getTownUUID())) {
            throw new IllegalArgumentException("Village already exists in map.");
        }
        villageMap.put(village.getTownUUID(), village);
    }

    private Village loadVillage(UUID villageUUID) throws IOException, ParseException {
        //Load Village from file
        File file = new File(plugin.getDataFolder(), "villages" + File.separator + villageUUID.toString() + ".json");
        if (!file.exists()) {
            throw new RuntimeException("Attempted to load village data for " + villageUUID + " but no file was found.");
        }
        JSONParser parser = new JSONParser();
        JSONObject villageData = (JSONObject) parser.parse(new FileReader(file));
        return new Village(villageUUID, villageData);
    }

    private void saveVillage(Village village) {
        //Save Village to file
        File fileO = new File(plugin.getDataFolder(), "villages" + File.separator + village.getTownUUID().toString() + ".json");
        try (FileWriter file = new FileWriter(fileO)) {
            file.write(village.getJsonObject().toJSONString());
            file.flush();
            village.setModified(false); //Reset modified flag
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving village data for " + village.getTownUUID() + " to JSON file.");
            plugin.logger(Level.WARNING, "Will retry on next save.");
            e.printStackTrace();
        }
    }
}
