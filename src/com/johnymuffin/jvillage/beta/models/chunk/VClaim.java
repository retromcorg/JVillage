package com.johnymuffin.jvillage.beta.models.chunk;

import com.johnymuffin.jvillage.beta.models.Village;

import java.util.UUID;

public class VClaim extends VChunk {
    //    private Village village;
    private UUID village;

    public VClaim(Village village, String worldName, int x, int z) {
        super(worldName, x, z);
        this.village = village.getTownUUID();
    }

    public VClaim(UUID village, VChunk vChunk) {
        super(vChunk.getWorldName(), vChunk.getX(), vChunk.getZ());
        this.village = village;
    }

    public VClaim(UUID village, String worldName, int x, int z) {
        super(worldName, x, z);
        this.village = village;
    }

    public UUID getVillage() {
        return village;
    }

    public VClaim(Village village, VChunk vChunk) {
        this(village, vChunk.getWorldName(), vChunk.getX(), vChunk.getZ());
    }
}
