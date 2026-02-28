package com.johnymuffin.jvillage.beta.listeners;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.integrations.BlueMapIntegration;
import com.johnymuffin.jvillage.beta.models.Village;
import org.bukkit.event.Listener;

/**
 * Listener to update BlueMap markers when village claims change
 */
public class BlueMapUpdateListener implements Listener {
    
    private final JVillage plugin;
    private final BlueMapIntegration blueMapIntegration;
    
    public BlueMapUpdateListener(JVillage plugin, BlueMapIntegration blueMapIntegration) {
        this.plugin = plugin;
        this.blueMapIntegration = blueMapIntegration;
    }
    
    /**
     * Generic method to schedule marker updates
     * Called whenever claims are modified
     */
    public void scheduleMarkerUpdate(final Village village) {
        if (!blueMapIntegration.isEnabled()) return;
        
        // Schedule update for next tick to batch multiple changes
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (village != null) {
                    blueMapIntegration.updateVillageMarkers(village);
                } else {
                    blueMapIntegration.updateAllMarkers();
                }
            }
        }, 20L); // 1 second delay to batch changes
    }
    
    /**
     * Schedule full marker refresh
     */
    public void scheduleFullUpdate() {
        if (!blueMapIntegration.isEnabled()) return;
        
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                blueMapIntegration.updateAllMarkers();
            }
        }, 20L);
    }
}
