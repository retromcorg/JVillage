package com.johnymuffin.jvillage.beta.config;

import com.johnymuffin.jvillage.beta.JVillage;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class JPlayerData extends Configuration {

    private JVillage plugin;

    private HashMap<String, HashMap<String, String>> playerData = new HashMap<String, HashMap<String, String>>();

    private HashMap<UUID, String> uuidToUsernameMap = new HashMap();
    private HashMap<String, UUID> usernameToUUIDMap = new HashMap();

    public JPlayerData(JVillage plugin) {
        super(new File(plugin.getDataFolder(), "players.yml"));
        this.plugin = plugin;
        load();

        if(getProperty("players") == null) {
            setProperty("players", (Object) new HashMap());
        }
        playerData = (HashMap<String, HashMap<String, String>>) getProperty("players");
        setHeader("#JVilage - Player Data", "#This file contains all player data for the plugin. Do not edit this file manually unless you know what you are doing");

        //Load all players into the UUID cache that have usernames
        for(String uuid : playerData.keySet()) {
            UUID uuidObj = UUID.fromString(uuid);
            if(hasPlayerData(uuidObj, "username")) {
                uuidToUsernameMap.put(uuidObj, getPlayerData(uuidObj, "username"));
                usernameToUUIDMap.put(getPlayerData(uuidObj, "username").toLowerCase(), uuidObj);
            }
        }
    }

    public void setPlayerData(UUID uuid, String key, String value) {
        HashMap<String, String> data = getPlayer(uuid);
        data.put(key, value);
        setPlayer(uuid, data);
    }

    public String getPlayerData(UUID uuid, String key) {
        HashMap<String, String> data = getPlayer(uuid);
        return data.get(key);
    }

    public Boolean getPlayerDataBoolean(UUID uuid, String key) {
        HashMap<String, String> data = getPlayer(uuid);
        if(!data.containsKey(key)) {
            return null;
        }
        return Boolean.parseBoolean(data.get(key));
    }

    public boolean hasPlayerData(UUID uuid, String key) {
        HashMap<String, String> data = getPlayer(uuid);
        return data.containsKey(key);
    }

    public UUID[] getAllPlayers() {
        UUID[] uuids = new UUID[playerData.size()];
        int i = 0;
        for(String uuid : playerData.keySet()) {
            uuids[i] = UUID.fromString(uuid);
            i++;
        }
        return uuids;
    }

    public boolean isPlayerKnown(UUID uuid) {
        return playerData.containsKey(uuid.toString());
    }

    private HashMap<String, String> getPlayer(UUID uuid) {
        if(!playerData.containsKey(uuid.toString())) {
            playerData.put(uuid.toString(), new HashMap<String, String>());
        }
        return playerData.get(uuid.toString());
    }

    private void setPlayer(UUID uuid, HashMap<String, String> data) {
        playerData.put(uuid.toString(), data);
    }

    public boolean save() {
        setProperty("players", (Object) playerData);
        return super.save();
    }

    //UUID/Username Functions

    public UUID getUUID(String playerName) {
        return usernameToUUIDMap.getOrDefault(playerName, null);
    }

    public void setUUID(String playerName, UUID uuid) {
        this.usernameToUUIDMap.put(playerName, uuid);
        this.uuidToUsernameMap.put(uuid, playerName);
        setPlayerData(uuid, "username", playerName);
    }

    public String getUsername(UUID uuid) {
        return uuidToUsernameMap.getOrDefault(uuid, null);
    }
}
