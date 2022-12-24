package com.johnymuffin.jvillage.beta.listeners;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.events.PlayerSwitchTownEvent;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class JVPlayerMoveListener extends CustomEventListener implements Listener {
    private JVillage plugin;

    public JVPlayerMoveListener(JVillage plugin) {
        this.plugin = plugin;

        //Register old style listeners
        Bukkit.getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, this, Event.Priority.Normal, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
//        System.out.println("Player moved");
        //If a player has changed block
        if ((event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
            Village village = plugin.getVillageAtLocation(event.getTo());
            plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId()).setCurrentlyLocatedIn(event.getPlayer(), village);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getTo());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getRespawnLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getPlayer().getLocation());
    }

    //TODO: This might not be needed, decide if it is or not
    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerChangedWorldEvent(final PlayerChangedWorldEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getPlayer().getLocation());
    }

    private void updatePlayerLocation(Player player, Location location) {
        Village village = plugin.getVillageAtLocation(location);
        plugin.getPlayerMap().getPlayer(player.getUniqueId()).setCurrentlyLocatedIn(player, village);
    }

    public void onPlayerSwitchTownEvent(final PlayerSwitchTownEvent event) {
        //Autoswitch to the new village
        boolean switchMessageSent = false;
        if (event.getNewVillage() != null) {
            VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
            //If player has auto switch enabled
            if (vPlayer.autoSwitchSelected()) {
                //If player is member of the town they have entered
                if (event.getNewVillage().isMember(event.getPlayer().getUniqueId())) {
                    //If player doesn't already have the town selected
                    if (vPlayer.getSelectedVillage() == null || !vPlayer.getSelectedVillage().equals(event.getNewVillage())) {
                        //Select new village
                        vPlayer.setSelectedVillage(event.getNewVillage());
                        String message = plugin.getLanguage().getMessage("movement_autoselect_enter");
                        message = message.replace("%village%", event.getNewVillage().getTownName());
                        event.getPlayer().sendMessage(message);
                        switchMessageSent = true;
                    }
                }
            }
        }
        //Send cross border message if they didn't get the auto switch message
        if (!switchMessageSent) {
            if (event.getNewVillage() != null) {
                String message = plugin.getLanguage().getMessage("movement_village_enter");
                message = message.replace("%village%", event.getNewVillage().getTownName());
                event.getPlayer().sendMessage(message);
            } else {
                String message = plugin.getLanguage().getMessage("movement_wilderness_enter");
                event.getPlayer().sendMessage(message);
            }
        }
    }

    //This isn't using event handlers because it's a custom event. Poseidon needs to be updated to support event handlers for custom events.
    //TODO: Update Poseidon to support custom events correctly (event handlers). Currently, when other custom events fire, this event will fire as well causing argument type mismatch exceptions.
    public void onCustomEvent(final Event preEvent) {
        //Forward event to the correct method
        if ((preEvent instanceof PlayerSwitchTownEvent)) {
            onPlayerSwitchTownEvent((PlayerSwitchTownEvent) preEvent);
        }
    }
}
