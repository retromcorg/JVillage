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

    /**
     * Gets the village the player was in before entering the new village.
     *
     * @return The village the player was in before switching, or null if the player was in wilderness.
     */
    public Village getOldVillage() {
        return oldVillage;
    }

    /**
     * Gets the village the player switched to.
     *
     * @return The village the player switched to, or null if the player has entered wilderness.
     */
    public Village getNewVillage() {
        return newVillage;
    }
}

