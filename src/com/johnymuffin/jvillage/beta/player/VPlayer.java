package com.johnymuffin.jvillage.beta.player;


import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.events.PlayerSwitchTownEvent;
import com.johnymuffin.jvillage.beta.models.Village;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class VPlayer {
    //Start - Village Memberships
    private ArrayList<Village> memberships = new ArrayList<>();
    //End - Village Memberships
    private UUID uuid;

    private JVillage plugin;

    private Village currentlyLocatedIn = null;


    public VPlayer(JVillage plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;

        //Start - Village Memberships
        for (UUID villageUUID : plugin.getVillageMap().getKnownVillages()) {
            Village village = plugin.getVillageMap().getVillage(villageUUID);
            if (village.isMember(uuid)) {
                memberships.add(village);
            }
        }
        //End - Village Memberships

    }

    public Village getCurrentlyLocatedIn() {
        return currentlyLocatedIn;
    }

    public void setCurrentlyLocatedIn(Player player, Village newlyLocatedIn) {
        if (currentlyLocatedIn != newlyLocatedIn) {
            //Fire event when player switches towns
            System.out.println("[Pre-Event] Player " + player.getName() + " has switched towns from " + (currentlyLocatedIn != null ? currentlyLocatedIn.getTownName() : "Wilderness") + " to " + (newlyLocatedIn != null ? newlyLocatedIn.getTownName() : "Wilderness"));
            PlayerSwitchTownEvent event = new PlayerSwitchTownEvent(player, currentlyLocatedIn, newlyLocatedIn);
            Bukkit.getPluginManager().callEvent(event);
            this.currentlyLocatedIn = newlyLocatedIn;
        }
    }

    public boolean isLocatedInVillage() {
        return currentlyLocatedIn != null;
    }

    public FundamentalsPlayer getFundamentalsPlayer() {
        return plugin.getFundamentals().getPlayerMap().getPlayer(uuid);
    }

    public String getUsername() {
        String username = plugin.getFundamentals().getPlayerCache().getUsernameFromUUID(uuid);
        if (username == null) {
            username = PoseidonUUID.getPlayerUsernameFromUUID(uuid);
        }
        if (username == null) {
            username = "Unknown UUID";
        }
        return username;
    }


    public ArrayList<Village> getMemberships() {
        return memberships;
    }

    public UUID getUUID() {
        return uuid;
    }

}
