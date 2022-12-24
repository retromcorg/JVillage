package com.johnymuffin.jvillage.beta.models;

import com.johnymuffin.jvillage.beta.interfaces.ClaimManager;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    private HashMap<String, ArrayList<VClaim>> claims = new HashMap<>(); //TODO: Use world UUID instead of world name
    private VCords townSpawn;

    private boolean modified = false;

    //Flags
    private boolean randomCanAlter = false;
    private boolean mobsCanSpawn = false;
    //    private boolean anyoneCanJoin = false;
    private boolean membersCanInvite = false;

    private ArrayList<UUID> invited = new ArrayList<UUID>();

    public Village(String townName, UUID townUUID, UUID owner, VChunk vChunk, VCords townSpawn) {
        this.townName = townName;
        this.townUUID = townUUID;
        this.owner = owner;
        System.out.println("[JVillage Debug] Claiming initial chunk: " + addClaim(this, vChunk));
        this.townSpawn = townSpawn;
        modified = true;
    }

    //Create village from JSON
    public Village(UUID uuid, JSONObject object) {
        this.townName = String.valueOf(object.get("name"));
        this.townUUID = uuid; // Ignore UUID in JSON file and use the one from the file name
        this.owner = UUID.fromString(String.valueOf(object.get("owner")));
        this.townSpawn = new VCords((JSONObject) object.get("townSpawn"));
        JSONArray members = (JSONArray) object.get("members");
        for (Object member : members) {
            this.members.add(UUID.fromString(String.valueOf(member)));
        }
        JSONArray assistants = (JSONArray) object.get("assistants");
        for (Object assistant : assistants) {
            this.assistants.add(UUID.fromString(String.valueOf(assistant)));
        }
        JSONArray claims = (JSONArray) object.get("claims");
        //Loop through worlds
        for (Object claim : claims) {
            JSONArray worldClaims = (JSONArray) claim;
            String worldName = String.valueOf(worldClaims.get(0));
            worldClaims.remove(0); //Remove world name from arrays

            //Skip to next world if world is not loaded
            //TODO: Add support for dynamically loading worlds
            if (Bukkit.getWorld(worldName) == null) {
                continue;
            }


            //Loop through claims in each world
            for (Object worldClaim : worldClaims) {
                JSONArray claimCords = (JSONArray) worldClaim;
                int x = Integer.parseInt(String.valueOf(claimCords.get(0)));
                int z = Integer.parseInt(String.valueOf(claimCords.get(1)));
                VChunk vChunk = new VChunk(worldName, x, z);
                addClaim(this, vChunk);
            }
        }

    }

    public JSONObject getJsonObject() {
        JSONObject object = new JSONObject();
        object.put("name", this.townName);
        object.put("owner", this.owner.toString());
        JSONArray members = new JSONArray();
        for (UUID member : this.members) {
            members.add(member.toString());
        }
        object.put("members", members);
        JSONArray assistants = new JSONArray();
        for (UUID assistant : this.assistants) {
            assistants.add(assistant.toString());
        }
        object.put("assistants", assistants);
        JSONArray claims = new JSONArray();
        for (String worldName : this.claims.keySet()) {
            JSONArray worldClaims = new JSONArray();
            worldClaims.add(worldName);
            for (VChunk vChunk : this.claims.get(worldName)) {
                JSONArray claimCords = new JSONArray();
                claimCords.add(vChunk.getX());
                claimCords.add(vChunk.getZ());
                worldClaims.add(claimCords);
            }
            claims.add(worldClaims);
        }
        object.put("claims", claims);
        object.put("townSpawn", this.townSpawn.getJsonObject());
        return object;
    }

    public void invitePlayer(UUID uuid) {
        this.invited.add(uuid);
    }

    public void uninvitePlayer(UUID uuid) {
        this.invited.remove(uuid);
    }

    public boolean isInvited(UUID uuid) {
        return this.invited.contains(uuid);
    }


    public boolean addClaim(VChunk vChunk) {
        modified = true; // Indicate that the village has been modified and needs to be saved

        //Create world array if it doesn't exist
        if (!claims.containsKey(vChunk.getWorldName())) {
            claims.put(vChunk.getWorldName(), new ArrayList<VClaim>());
        }
//        //Check if chunk is already claimed
//        if (claims.get(vChunk.getWorldName()).contains(new VClaim(this, vChunk))) {
//            return false;
//        }
        //Add chunk to claims
        claims.get(vChunk.getWorldName()).add(new VClaim(this, vChunk));
        return true;
    }

    public boolean addClaim(Village village, VChunk vChunk) {
        return village.addClaim(vChunk);
    }

    public boolean removeClaim(VChunk vChunk) {
        modified = true; // Indicate that the village has been modified and needs to be saved
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

    public boolean canPlayerAlter(Player player) {
        if (this.randomCanAlter) {
            return true;
        }
        if (isMember(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
        modified = true;
    }

    public UUID getTownUUID() {
        return townUUID;
    }

    public UUID[] getMembers() {
        return members.toArray(new UUID[members.size()]);
    }


    public void addMember(UUID uuid) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        members.add(uuid);
    }

    public UUID[] getAssistants() {
        return assistants.toArray(new UUID[assistants.size()]);
    }

    public void removeAssistant(UUID uuid) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        assistants.remove(uuid);

        //Remove from members if they are in there
        members.remove(uuid);
    }

    public void addAssistant(UUID uuid) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        assistants.add(uuid);

        //Remove from members if they are in there
        members.remove(uuid);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID uuid) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        owner = uuid;
    }

//    public HashMap<String, ArrayList<VChunk>> getClaims() {
//        return claims;
//    }

    public VCords getTownSpawn() {
        return townSpawn;
    }

    public void setTownSpawn(VCords cords) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        townSpawn = cords;
    }

    public boolean isMember(UUID uuid) {
        if (members.contains(uuid)) {
            return true;
        }
        if (assistants.contains(uuid)) {
            return true;
        }

        if (owner.equals(uuid)) {
            return true;
        }

        return false;
    }

    public boolean isAssistant(UUID uuid) {
        if (assistants.contains(uuid)) {
            return true;
        }

        if (owner.equals(uuid)) {
            return true;
        }

        return false;
    }

    public boolean isOwner(UUID uuid) {
        if (owner.equals(uuid)) {
            return true;
        }

        return false;
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean removePlayerFromVillage(UUID uuid) {
        if (isOwner(uuid)) {
            throw new IllegalArgumentException("Cannot remove owner from village");
        }
        if (isAssistant(uuid)) {
            removeAssistant(uuid);
            return true;
        }
        if (isMember(uuid)) {
            removeMember(uuid);
            return true;
        }
        return false;
    }

    public ArrayList<VChunk> getClaims() {
        ArrayList<VChunk> vChunks = new ArrayList<>();
        for(String world : claims.keySet()){
            vChunks.addAll(claims.get(world));
        }
        return vChunks;
    }

    public ArrayList<VClaim> getClaims(String world) {
        if (claims.containsKey(world)) {
            return claims.get(world);
        }
        ArrayList<VClaim> empty = new ArrayList<VClaim>();
        claims.put(world, empty);
        return empty;
    }

    public String[] getClaimedWorlds() {
        return claims.keySet().toArray(new String[claims.keySet().size()]);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }


    public boolean isRandomCanAlter() {
        return randomCanAlter;
    }

    public void setRandomCanAlter(boolean randomCanAlter) {
        this.randomCanAlter = randomCanAlter;
    }

    public boolean isMobsCanSpawn() {
        return mobsCanSpawn;
    }

    public void setMobsCanSpawn(boolean mobsCanSpawn) {
        this.mobsCanSpawn = mobsCanSpawn;
    }

    public boolean isMembersCanInvite() {
        return membersCanInvite;
    }

    public void setMembersCanInvite(boolean membersCanInvite) {
        this.membersCanInvite = membersCanInvite;
    }

    public int getTotalClaims() {
        int total = 0;
        for (String world : claims.keySet()) {
            total += claims.get(world).size();
        }
        return total;
    }
}
