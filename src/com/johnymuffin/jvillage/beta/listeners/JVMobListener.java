package com.johnymuffin.jvillage.beta.listeners;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class JVMobListener implements Listener {
    private JVillage plugin;

    public JVMobListener(JVillage plugin) {
        this.plugin = plugin;
    }


    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Lowest)
    public void onMobSpawnEvent(final CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof CraftMonster)) {
            return;
        }
        //See if the mob is in a village
        Village village = plugin.getVillageAtLocation(event.getLocation());
        if (village == null) {
            return;
        }
        if (village.isMobsCanSpawn()) {
            return;
        }
        //Cancel the spawn as the mob is in a village and mobs are not allowed to spawn
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Lowest)
    public void onEntityDamageEvnet(final EntityDamageByEntityEvent event) {
        //Return if the victim is not a player
        if (!(event.getEntity() instanceof CraftPlayer)) {
            return;
        }

        //Return if the damager is not a hostile mob
        if (!(event.getDamager() instanceof CraftMonster)) {
            return;
        }

        Player player = (Player) event.getEntity();
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        //Return if the player is not in a village
        if (vPlayer.isLocatedInVillage() == false) {
            return;
        }

        if(vPlayer.getCurrentlyLocatedIn().isMobsCanSpawn()) {
            return;
        }

        //Cancel the damage event if it is a hostile mob attacking a player in a village where mobs are not allowed to spawn
        event.setCancelled(true);

        //Kill the hostile mob
        event.getDamager().remove();
    }


}
