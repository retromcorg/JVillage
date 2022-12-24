package com.johnymuffin.jvillage.beta.events;

import com.johnymuffin.jvillage.beta.models.Village;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSwitchTownEvent extends Event {
    private Village oldVillage;
    private Village newVillage;
    private Player player;

    public PlayerSwitchTownEvent(Player player, Village oldVillage, Village newVillage) {
        super("PlayerSwitchTownEvent");
        this.player = player;
        this.oldVillage = oldVillage;
        this.newVillage = newVillage;
    }

    public Player getPlayer() {
        return player;
    }

    public Village getOldVillage() {
        return oldVillage;
    }

    public Village getNewVillage() {
        return newVillage;
    }
}

