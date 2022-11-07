package com.johnymuffin.jvillage.beta.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Village implements ClaimManager {
    private String townName;
    private UUID townUUID;
    private ArrayList<UUID> members = new ArrayList<UUID>();
    private ArrayList<UUID> assistants = new ArrayList<UUID>();
    private UUID owner;
    private HashMap<String, ArrayList<VChunk>> claims = new HashMap<>();
    private VCords townSpawn;


    public Village(String townName, UUID townUUID, UUID owner, VChunk vChunk, VCords townSpawn) {
        this.townName = townName;
        this.townUUID = townUUID;
        this.owner = owner;
        addClaim(this, vChunk);
        this.townSpawn = townSpawn;
    }

    public boolean addClaim(Village village, VChunk vChunk) {
        if (!claims.containsKey(vChunk.getWorldName())) {
            claims.put(vChunk.getWorldName(), new ArrayList<VChunk>());
        }
        if (claims.get(vChunk.getWorldName()).contains(vChunk)) {
            return false;
        }
        claims.get(vChunk.getWorldName()).add(vChunk);
        return true;
    }

    public boolean removeClaim(VChunk vChunk) {
        if (!claims.containsValue(vChunk)) {
            return false;
        }
        claims.remove(vChunk);
        return true;
    }

    public boolean isClaimed(VChunk vChunk) {
        return claims.containsValue(vChunk);
    }


    public Village(JSONObject jsonObject) {
        townName = (String) jsonObject.get("name");
        townUUID = UUID.fromString((String) jsonObject.get("uuid"));
        //Load Member List
        for (Object member : (JSONArray) jsonObject.get("members")) {
            this.members.add(UUID.fromString((String) member));
        }
        //Load Assistant List
        for (Object assistant : (JSONArray) jsonObject.get("assistants")) {
            this.assistants.add(UUID.fromString((String) assistant));
        }
        this.owner = UUID.fromString((String) jsonObject.get("owner"));
        //Load Claims
        JSONArray claims = (JSONArray) jsonObject.get("claims");
        for (Object worldObject : claims) {
            JSONObject world = (JSONObject) worldObject;
            String worldName = (String) world.get("world");
            JSONArray worldClaims = (JSONArray) world.get("claims");
            for (Object claimObject : worldClaims) {
                String claim = (String) claimObject;
                String cords[] = claim.split(".");
                VChunk vChunk = new VChunk(worldName, Integer.parseInt(cords[0]), Integer.parseInt(cords[1]));
                claims.add(vChunk);
            }
        }
        //Load Town Spawn
        JSONObject townSpawn = (JSONObject) jsonObject.get("townSpawn");
        this.townSpawn = new VCords(Integer.parseInt((String) townSpawn.get("x")), Integer.parseInt((String) townSpawn.get("y")), Integer.parseInt((String) townSpawn.get("z")), (String) townSpawn.get("world"));

    }


    public String getTownName() {
        return townName;
    }

    public UUID getTownUUID() {
        return townUUID;
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public ArrayList<UUID> getAssistants() {
        return assistants;
    }

    public UUID getOwner() {
        return owner;
    }

    public HashMap<String, ArrayList<VChunk>> getClaims() {
        return claims;
    }

    public VCords getTownSpawn() {
        return townSpawn;
    }
}
