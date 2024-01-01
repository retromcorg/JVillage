package com.johnymuffin.jvillage.beta.interfaces;

import com.johnymuffin.jvillage.beta.models.chunk.VChunk;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;

public interface ClaimManager {

    public boolean addClaim(VClaim vChunk);

    public boolean removeClaim(VClaim vChunk);

    public boolean isClaimed(VChunk vChunk);

}
