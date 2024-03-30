package com.johnymuffin.jvillage.beta.tasks;

import com.johnymuffin.jvillage.beta.JVillage;

import java.util.logging.Level;

public class AutomaticSaving implements Runnable {

    private JVillage plugin;

    public AutomaticSaving(JVillage plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.debugLogger(Level.INFO, "Running auto saving task");

        plugin.getVillageMap().saveData();
        plugin.getPlayerData().save();
    }
}
