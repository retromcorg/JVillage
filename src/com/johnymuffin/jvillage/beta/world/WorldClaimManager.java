package com.johnymuffin.jvillage.beta.world;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.interfaces.ClaimManager;
import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import com.johnymuffin.jvillage.beta.models.Village;

import java.util.ArrayList;

public class WorldClaimManager implements ClaimManager {
    private ArrayList<VClaim> claims = new ArrayList<>();
    private String worldName;
    private JVillage jVillage;

    public WorldClaimManager(JVillage plugin, String worldName) {
        this.jVillage = plugin;
        this.worldName = worldName;
    }


    public boolean addClaim(Village village, VChunk vChunk) {
        claims.add(new VClaim(village, vChunk));
        return true;
    }

    public boolean removeClaim(VChunk vChunk) {
        return claims.remove(vChunk);
    }

    public boolean isClaimed(VChunk vChunk) {
        return claims.contains(vChunk);
    }

    public Village getVillageAtChunk(VChunk vChunk) {
        Village village = null;
        if (claims.contains(vChunk)) {
            for (VClaim vClaim : claims) {
                if (vClaim.equals(vChunk)) {
                    village = jVillage.getVillageMap().getVillage(vClaim.getVillage());
                }
            }
        }
        return village;
    }

}
