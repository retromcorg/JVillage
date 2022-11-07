package com.johnymuffin.jvillage.beta.models;

public interface ClaimManager {

    public boolean addClaim(Village village, VChunk vChunk);

    public boolean removeClaim(VChunk vChunk);

    public boolean isClaimed(VChunk vChunk);

}
