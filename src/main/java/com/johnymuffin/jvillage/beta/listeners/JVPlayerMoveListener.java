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

import java.util.ArrayList;

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

        //Disable auto claiming when a player teleports if they have it enabled
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        vPlayer.setAutoClaimingEnabled(false, true);
        vPlayer.setAutoUnclaimingEnabled(false, true);
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getRespawnLocation());

        //Disable auto claiming when a player respawns if they have it enabled
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        vPlayer.setAutoClaimingEnabled(false, true);
        vPlayer.setAutoUnclaimingEnabled(false, true);
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getPlayer().getLocation());

        //Disable auto claiming when a player joins if they have it enabled
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        vPlayer.setAutoClaimingEnabled(false, false); //Don't send message as it shouldn't be enabled anyway
        vPlayer.setAutoUnclaimingEnabled(false, false);

        //Record appropriate player data
        String firstJoin = plugin.getPlayerData().getPlayerData(event.getPlayer().getUniqueId(), "firstJoin");
        if (firstJoin == null) {
            plugin.getPlayerData().setPlayerData(event.getPlayer().getUniqueId(), "firstJoin", String.valueOf(System.currentTimeMillis()/1000L));
        }
        plugin.getPlayerData().setPlayerData(event.getPlayer().getUniqueId(), "username", event.getPlayer().getName());
        plugin.getPlayerData().setPlayerData(event.getPlayer().getUniqueId(), "lastOnline", String.valueOf(System.currentTimeMillis()/1000L));
        plugin.getPlayerData().setUUID(vPlayer.getUsername(), event.getPlayer().getUniqueId());

        ArrayList<Village> invites = vPlayer.getInvitedToVillages();
        if (!invites.isEmpty()) {
            event.getPlayer().sendMessage(plugin.getLanguage().getMessage("notification_invites"));
        }
    }

    //TODO: This might not be needed, decide if it is or not
    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerChangedWorldEvent(final PlayerChangedWorldEvent event) {
        updatePlayerLocation(event.getPlayer(), event.getPlayer().getLocation());

        //Disable auto claiming when a player changes world if they have it enabled
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        vPlayer.setAutoClaimingEnabled(false, true);
        vPlayer.setAutoUnclaimingEnabled(false, true);
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        //Disable auto claiming when a player quits if they have it enabled
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        vPlayer.setAutoClaimingEnabled(false, false);
        vPlayer.setAutoUnclaimingEnabled(false, false);

        plugin.getPlayerData().setPlayerData(event.getPlayer().getUniqueId(), "lastOnline", String.valueOf(System.currentTimeMillis()/1000L));
    }

    private void updatePlayerLocation(Player player, Location location) {
        Village village = plugin.getVillageAtLocation(location);
        plugin.getPlayerMap().getPlayer(player.getUniqueId()).setCurrentlyLocatedIn(player, village);
    }

    public void onPlayerSwitchTownEvent(final PlayerSwitchTownEvent event) {
        //Autoswitch to the new village
        boolean switchMessageSent = false;
        VPlayer vPlayer = plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        if (event.getNewVillage() != null) {
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
                vPlayer.setAutoClaimingEnabled(false, true); //Disable auto claiming when a player crosses a border to another village
                String message = plugin.getLanguage().getMessage("movement_village_enter");
                message = message.replace("%village%", event.getNewVillage().getTownName());
                event.getPlayer().sendMessage(message);
            } else {
                String message = plugin.getLanguage().getMessage("movement_wilderness_enter");
                event.getPlayer().sendMessage(message);

                //Auto Claiming logic if enabled
//                if (vPlayer.isAutoClaimingEnabled()) {
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
//                        @Override
//                        public void run() {
//                            //Auto claim after one tick to prevent exceptions
//                        }
//                    }, 1L);
//
//                }
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
