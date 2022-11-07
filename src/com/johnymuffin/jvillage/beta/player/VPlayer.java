package com.johnymuffin.jvillage.beta.player;


import com.johnymuffin.jvillage.beta.models.Village;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class VPlayer {
    //Start - Village Memberships
    private ArrayList<Village> memberships = new ArrayList<>();
    private ArrayList<Village> assisting = new ArrayList<>();
    private UUID owning;
    //End - Village Memberships
    private UUID uuid;
    private String lastUsername;


    public VPlayer(UUID uuid) {
        this.uuid = uuid;
    }


    public VPlayer(JSONObject jsonObject) {
        this.uuid = UUID.fromString((String) jsonObject.get("uuid"));
        this.lastUsername = (String) jsonObject.get("lastUsername");

    }


    public ArrayList<Village> getMemberships() {
        return memberships;
    }

    public ArrayList<Village> getAssisting() {
        return assisting;
    }

    public UUID getOwning() {
        return owning;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastUsername() {
        return lastUsername;
    }
}
