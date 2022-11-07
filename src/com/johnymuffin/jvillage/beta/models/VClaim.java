package com.johnymuffin.jvillage.beta.models;

public class VClaim extends VChunk {
    private Village village;

    public VClaim(Village village, String worldName, int x, int z) {
        super(worldName, x, z);
        this.village = village;
    }

    public Village getVillage() {
        return village;
    }

    public VClaim(Village village, VChunk vChunk) {
        this(village, vChunk.getWorldName(), vChunk.getX(), vChunk.getZ());
    }
}
