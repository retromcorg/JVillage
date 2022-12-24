package com.johnymuffin.jvillage.beta.interfaces;

import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.Village;

public interface ClaimManager {

    public boolean addClaim(Village village, VChunk vChunk);

    public boolean removeClaim(VChunk vChunk);

    public boolean isClaimed(VChunk vChunk);

}
