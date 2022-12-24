package com.johnymuffin.jvillage.beta;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JPlayerMap {
    private JVillage plugin;

    private HashMap<UUID, VPlayer> playerMap = new HashMap<>();

    public JPlayerMap(JVillage plugin) {
        this.plugin = plugin;

        for (UUID uuid : getFundamentals().getPlayerMap().getKnownPlayers()) {
            playerMap.put(uuid, new VPlayer(plugin, uuid));
        }

    }

    public VPlayer getPlayer(UUID uuid) {
        if (!playerMap.containsKey(uuid)) {
            playerMap.put(uuid, new VPlayer(plugin, uuid));
        }
        return playerMap.get(uuid);
    }


    private Fundamentals getFundamentals() {
        return plugin.getFundamentals();
    }


}
