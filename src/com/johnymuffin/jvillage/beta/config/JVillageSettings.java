package com.johnymuffin.jvillage.beta.config;

import com.johnymuffin.jvillage.beta.JVillage;
import org.bukkit.util.config.Configuration;

import java.io.File;

public class JVillageSettings extends Configuration {

    public JVillageSettings(File settingsFile) {
        super(settingsFile);
        this.reload();
    }

    private void write() {
        //Main
        generateConfigOption("config-version", 1);

        //Setting
        generateConfigOption("settings.town-create.price.amount", 1000);
        generateConfigOption("settings.town-create.price.info", "This is the price to create a town. Set to 0 to disable.");

        generateConfigOption("settings.town-claim.price.amount", 10);
        generateConfigOption("settings.town-claim.price.info", "This is the price to claim a chunk. Set to 0 to disable.");

        generateConfigOption("settings.town-claim-outpost.price.amount", 500);
        generateConfigOption("settings.town-claim-outpost.price.info", "This is the price to claim an outpost. Set to 0 to disable.");

        generateConfigOption("settings.resident.maximum-towns-owned.value", 10);
        generateConfigOption("settings.resident.maximum-towns-owned.info", "This is the maximum number of towns a resident can own. Set to 0 to disable.");

        generateConfigOption("settings.resident.maximum-towns-joined.value", 10);
        generateConfigOption("settings.resident.maximum-towns-joined.info", "This is the maximum number of towns a resident can join. Set to 0 to disable.");


    }

    public void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }


    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End


    public Long getConfigLongOption(String key) {
        if (this.getConfigOption(key) == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(this.getProperty(key)));
    }


    private boolean convertToNewAddress(String newKey, String oldKey) {
        if (this.getString(newKey) != null) {
            return false;
        }
        if (this.getString(oldKey) == null) {
            return false;
        }
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;

    }


    private void reload() {
        this.load();
        this.write();
        this.save();
    }
}
