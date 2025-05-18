package com.johnymuffin.jvillage.beta.listeners;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;

public class JVMobListener extends EntityListener implements Listener {
    private JVillage plugin;

    public JVMobListener(JVillage plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE_BY_ENTITY, this, Event.Priority.Normal, plugin);
    }


    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Lowest)
    public void onMobSpawnEvent(final CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Monster || event.getEntity().getClass().getSimpleName().equals("CraftSlime"))) {
            return;
        }
        //See if the mob is in a village
        Village village = plugin.getVillageAtLocation(event.getLocation());
        if (village == null) {
            return;
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && village.isMobSpawnerBypass()) {
            //Allow the mob to spawn if it is from a spawner, and the village has mob spawner bypass enabled
            return;
        }

        if (village.isMobsCanSpawn()) {
            return;
        }
        //Cancel the spawn as the mob is in a village and mobs are not allowed to spawn
        event.setCancelled(true);
//        System.out.println("Blocked a hostile mob from spawning in a village");
    }


    @EventHandler(priority = Event.Priority.Normal)
    public void onEntityDamageEvnet(final EntityDamageEvent preEvent) {
        if (!(preEvent instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) preEvent;

        CraftEntity damager = (CraftEntity) event.getDamager();

        //Return if the victim is not a player
        if (!(event.getEntity() instanceof CraftPlayer)) {
//            System.out.println("EntityDamageByEntityEvent: " + event.getEntity().toString());
            return;
        }
//        System.out.println("Player was damaged");

        //Return if the damager is not a hostile mob
        if (!(damager instanceof Monster)) {
            if (damager instanceof CraftArrow) {
                CraftArrow arrow = (CraftArrow) event.getDamager();
                damager = (CraftEntity) arrow.getShooter();
                //Return if the damager is not a hostile mob
                if (!(damager instanceof Monster)) {
                    return;
                }
            } else {
                return;
            }
        }
//        System.out.println("Player was damaged by a hostile mob");

        Player player = (Player) event.getEntity();
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(player.getUniqueId());

        //Return if the player is not in a village
        if (vPlayer.isLocatedInVillage() == false) {
            return;
        }
//        System.out.println("Player was damaged by a hostile mob in a village");

        if (vPlayer.getCurrentlyLocatedIn().isMobsCanSpawn()) {
            return;
        }
//        System.out.println("Player was damaged by a hostile mob in a village where mobs are not allowed to spawn");

        //Cancel the damage event if it is a hostile mob attacking a player in a village where mobs are not allowed to spawn
        event.setCancelled(true);

        //Kill the hostile mob
        damager.teleport(damager.getLocation().subtract(0, 300, 0)); //Teleport the mob to the void
    }


}
