package com.johnymuffin.jvillage.beta.models;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.interfaces.ClaimManager;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class Village implements ClaimManager {
    private JVillage plugin;
    private String townName;
    private UUID townUUID;
    private ArrayList<UUID> members = new ArrayList<UUID>();
    private ArrayList<UUID> assistants = new ArrayList<UUID>();
    private UUID owner;
    private VCords townSpawn;

    private boolean modified = false;

    private HashMap<VillageFlags, Boolean> flags = new HashMap<VillageFlags, Boolean>();

    private ArrayList<UUID> invited = new ArrayList<UUID>();

    private void initializeFlags() {
        for (VillageFlags flag : VillageFlags.values()) {
            flags.put(flag, false);
        }
    }

    public Village(JVillage plugin, String townName, UUID townUUID, UUID owner, VChunk vChunk, VCords townSpawn) {
        this.plugin = plugin;
        this.townName = townName;
        this.townUUID = townUUID;
        this.owner = owner;
        System.out.println("[JVillage Debug] Claiming initial chunk: " + addClaim(new VClaim(this, vChunk)));
        this.townSpawn = townSpawn;
        modified = true;
        initializeFlags();
    }

    //Create village from JSON
    public Village(JVillage plugin, UUID uuid, JSONObject object) {
        this.plugin = plugin;
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
//                VChunk vChunk = new VChunk(worldName, x, z);
                VClaim vClaim = new VClaim(this.getTownUUID(), worldName, x, z);
//                if (this.plugin.isClaimed(vClaim)) {
//                    Village village = this.plugin.getVillageAtLocation(vClaim);
//                    plugin.logger(Level.WARNING, "Skipping claim: " + vClaim.toString() + " for " + getTownName() + " as it is already claimed by " + village.getTownName() + ". It is advised that you delete this claim from the JSON file or unclaim it with \"/va village unclaim\" while standing in it.");
//                    //Possibly a continued here? For now I'll leave it up to admins to fix
//                }
                addClaim(vClaim);
            }
        }
        initializeFlags();
        //Load flags saved
        JSONObject flags = (JSONObject) object.getOrDefault("flags", new JSONObject());
        for (Object flag : flags.keySet()) {
            this.flags.put(VillageFlags.valueOf(String.valueOf(flag)), Boolean.parseBoolean(String.valueOf(flags.get(flag))));
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
        JSONArray claimsJsonArray = new JSONArray();
        for (String worldName : this.getWorldsWithClaims()) {
            JSONArray worldClaims = new JSONArray();
            worldClaims.add(worldName);
            for (VClaim vClaim : this.getClaimsInWorld(worldName)) {
                JSONArray claimCords = new JSONArray();
                claimCords.add(vClaim.getX());
                claimCords.add(vClaim.getZ());
                worldClaims.add(claimCords);
            }
            claimsJsonArray.add(worldClaims);
        }

        //Save Flags
        JSONObject flags = new JSONObject();
        for (VillageFlags flag : this.flags.keySet()) {
            flags.put(flag.toString(), this.flags.get(flag));
        }
        object.put("flags", flags);

        object.put("claims", claimsJsonArray);
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


    public boolean addClaim(VClaim vChunk) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        return plugin.getVillageClaimsArray(this).add(vChunk);
    }

    public boolean removeClaim(VClaim vChunk) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        return plugin.getVillageClaimsArray(this).remove(vChunk);
    }

    public boolean removeClaim(VChunk vChunk) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        return plugin.getVillageClaimsArray(this).remove(vChunk);
    }

    public boolean isClaimed(VChunk vChunk) {
        return plugin.getVillageClaimsArray(this).contains(vChunk);
    }


//    public Village(JSONObject jsonObject) {
//        townName = (String) jsonObject.get("name");
//        townUUID = UUID.fromString((String) jsonObject.get("uuid"));
//        //Load Member List
//        for (Object member : (JSONArray) jsonObject.get("members")) {
//            this.members.add(UUID.fromString((String) member));
//        }
//        //Load Assistant List
//        for (Object assistant : (JSONArray) jsonObject.get("assistants")) {
//            this.assistants.add(UUID.fromString((String) assistant));
//        }
//        this.owner = UUID.fromString((String) jsonObject.get("owner"));
//        //Load Claims
//        JSONArray claims = (JSONArray) jsonObject.get("claims");
//        for (Object worldObject : claims) {
//            JSONObject world = (JSONObject) worldObject;
//            String worldName = (String) world.get("world");
//            JSONArray worldClaims = (JSONArray) world.get("claims");
//            for (Object claimObject : worldClaims) {
//                String claim = (String) claimObject;
//                String cords[] = claim.split(".");
//                VChunk vChunk = new VChunk(worldName, Integer.parseInt(cords[0]), Integer.parseInt(cords[1]));
//                claims.add(vChunk);
//            }
//        }
//        //Load Town Spawn
//        JSONObject townSpawn = (JSONObject) jsonObject.get("townSpawn");
//        this.townSpawn = new VCords(Integer.parseInt((String) townSpawn.get("x")), Integer.parseInt((String) townSpawn.get("y")), Integer.parseInt((String) townSpawn.get("z")), (String) townSpawn.get("world"));
//
//    }

    public boolean canPlayerAlter(Player player) {
        if (isRandomCanAlter()) {
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

        //Remove the new owner from the members list or assistants list
        //TODO: This shouldn't be handled in this low level function. Move later
        if (members.contains(uuid)) {
            members.remove(uuid);
        }

        if (assistants.contains(uuid)) {
            assistants.remove(uuid);
        }
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
        modified = true; // Indicate that the village has been modified and needs to be saved
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

    public ArrayList<VClaim> getClaims() {
        return plugin.getVillageClaimsArray(this);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        this.modified = modified;
    }


    public boolean isRandomCanAlter() {
        return this.flags.get(VillageFlags.RANDOM_CAN_ALTER);
    }

    public void setRandomCanAlter(boolean randomCanAlter) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        this.flags.put(VillageFlags.RANDOM_CAN_ALTER, randomCanAlter);
    }

    public boolean isMobsCanSpawn() {
        return this.flags.get(VillageFlags.MOBS_CAN_SPAWN);
    }

    public void setMobsCanSpawn(boolean mobsCanSpawn) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        this.flags.put(VillageFlags.MOBS_CAN_SPAWN, mobsCanSpawn);
    }

    public boolean isMobSpawnerBypass() {
        return this.flags.get(VillageFlags.MOB_SPAWNER_BYPASS);
    }

    public void setMobSpawnerBypass(boolean mobSpawnerBypass) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        this.flags.put(VillageFlags.MOB_SPAWNER_BYPASS, mobSpawnerBypass);
    }

    public boolean isMembersCanInvite() {
        return this.flags.get(VillageFlags.MEMBERS_CAN_INVITE);
    }

    public void setMembersCanInvite(boolean membersCanInvite) {
        modified = true; // Indicate that the village has been modified and needs to be saved
        this.flags.put(VillageFlags.MEMBERS_CAN_INVITE, membersCanInvite);
    }

    public int getTotalClaims() {
        return getClaims().size();
    }

    public void broadcastToTown(String message) {
        String broadcastMessage = ChatColor.GOLD + "[" + ChatColor.AQUA + "Village: " + getTownName() + ChatColor.GOLD + "] " + ChatColor.GRAY + message;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(broadcastMessage);
            }
        }
    }

    public String[] getWorldsWithClaims() {
        ArrayList<String> worlds = new ArrayList<>();
        for (VClaim vClaim : getClaims()) {
            if (!worlds.contains(vClaim.getWorldName())) {
                worlds.add(vClaim.getWorldName());
            }
        }
        return worlds.toArray(new String[worlds.size()]);
    }

    private VClaim[] getClaimsInWorld(String world) {
        ArrayList<VClaim> vClaims = new ArrayList<>();
        for (VClaim vClaim : getClaims()) {
            if (vClaim.getWorldName().equalsIgnoreCase(world)) {
                vClaims.add(vClaim);
            }
        }
        return vClaims.toArray(new VClaim[vClaims.size()]);
    }

    public HashMap<VillageFlags, Boolean> getFlags() {
        return flags;
    }
}
