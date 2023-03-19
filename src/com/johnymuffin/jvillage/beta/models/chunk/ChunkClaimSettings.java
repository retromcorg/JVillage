package com.johnymuffin.jvillage.beta.models.chunk;

import com.johnymuffin.jvillage.beta.models.Village;
import org.json.simple.JSONObject;

import java.util.UUID;

public class ChunkClaimSettings extends VChunk{

    private Village village;
    private final long claimTime;
    private final UUID claimedBy;

    public ChunkClaimSettings(Village village, JSONObject jsonObject, String worldName) {
        super(worldName, Integer.valueOf(String.valueOf(jsonObject.get("x"))), Integer.valueOf(String.valueOf(jsonObject.get("z"))));
        this.claimTime = (long) jsonObject.get("claimTime");
        this.claimedBy = UUID.fromString((String) jsonObject.get("claimedBy"));
    }

    public ChunkClaimSettings(Village village, long claimTime, UUID claimedBy, VChunk vChunk) {
        super(vChunk.getWorldName(), vChunk.getX(), vChunk.getZ());
        this.claimTime = claimTime;
        this.claimedBy = claimedBy;
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("claimTime", claimTime);
        jsonObject.put("claimedBy", claimedBy.toString());
        jsonObject.put("x", this.getX());
        jsonObject.put("z", this.getZ());
        return jsonObject;
    }




}
