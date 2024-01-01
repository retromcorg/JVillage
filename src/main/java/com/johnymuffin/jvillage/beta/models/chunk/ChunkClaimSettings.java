package com.johnymuffin.jvillage.beta.models.chunk;

import com.johnymuffin.jvillage.beta.models.Village;
import org.json.simple.JSONObject;

import java.util.UUID;

public class ChunkClaimSettings extends VChunk{

    private Village village;
    private final long claimTime;
    private final UUID claimedBy;
    private double price;

    public ChunkClaimSettings(Village village, JSONObject jsonObject, String worldName) {
        super(worldName, Integer.valueOf(String.valueOf(jsonObject.get("x"))), Integer.valueOf(String.valueOf(jsonObject.get("z"))));
        this.claimTime = (long) jsonObject.get("claimTime");
        this.claimedBy = UUID.fromString((String) jsonObject.get("claimedBy"));
        this.price = Double.valueOf(String.valueOf(jsonObject.getOrDefault("price", 0)));
    }

    public ChunkClaimSettings(Village village, long claimTime, UUID claimedBy, VChunk vChunk, double price) {
        super(vChunk.getWorldName(), vChunk.getX(), vChunk.getZ());
        this.claimTime = claimTime;
        this.claimedBy = claimedBy;
        this.price = price;
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("claimTime", claimTime);
        jsonObject.put("claimedBy", claimedBy.toString());
        jsonObject.put("x", this.getX());
        jsonObject.put("z", this.getZ());
        jsonObject.put("price", price);
        return jsonObject;
    }

    public long getClaimTime() {
        return claimTime;
    }

    public UUID getClaimedBy() {
        return claimedBy;
    }

    public double getPrice() {
        return price;
    }

}
