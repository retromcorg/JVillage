package com.johnymuffin.jvillage.beta.world;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.ClaimManager;
import com.johnymuffin.jvillage.beta.models.VChunk;
import com.johnymuffin.jvillage.beta.models.Village;

import java.util.HashMap;
import java.util.UUID;

public class VWorldClaims implements ClaimManager {
    private HashMap<UUID, VChunk> claims = new HashMap<>();
    private String worldName;
    private JVillage jVillage;

    public VWorldClaims(JVillage plugin, String worldName) {
        this.jVillage = plugin;
        this.worldName = worldName;
    }


    public boolean addClaim(Village village, VChunk vChunk) {
        if (claims.containsValue(vChunk)) {
            return false;
        }
        claims.put(village.getTownUUID(), vChunk);
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

}
