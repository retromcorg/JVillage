package com.johnymuffin.jvillage.beta.config;

import com.johnymuffin.jvillage.beta.JVillage;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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

        generateConfigOption("settings.town.max-name-length.value", 20);
        generateConfigOption("settings.town.max-name-length.info", "This is the maximum length of a town name.");

        generateConfigOption("settings.town-claim.price.amount", 10);
        generateConfigOption("settings.town-claim.price.info", "This is the price to claim a chunk. Set to 0 to disable.");

        generateConfigOption("settings.town-claim-outpost.price.amount", 500);
        generateConfigOption("settings.town-claim-outpost.price.info", "This is the price to claim an outpost. Set to 0 to disable.");

        generateConfigOption("settings.resident.maximum-towns-owned.value", 10);
        generateConfigOption("settings.resident.maximum-towns-owned.info", "This is the maximum number of towns a resident can own. Set to 0 to disable.");

        generateConfigOption("settings.resident.maximum-towns-joined.value", 10);
        generateConfigOption("settings.resident.maximum-towns-joined.info", "This is the maximum number of towns a resident can join. Set to 0 to disable.");

        generateConfigOption("settings.world-guard.blocked-regions.info", "Claims will not be allowed by players within these WorldGuard regions.");
        generateConfigOption("settings.world-guard.blocked-regions.enabled", true);

        generateConfigOption("settings.town-create.claim-radius.enabled", false);
        generateConfigOption("settings.town-create.claim-radius.value", 128);
        generateConfigOption("settings.town-create.claim-radius.info", "How far away a town creation claim has to be from other towns");

        generateConfigOption("settings.always-use-default-lang.info", "If true, the default language will always be used, even if the player has a language set.");
        generateConfigOption("settings.always-use-default-lang.enabled", false);

        getWorldGuardPermissions(); //This is a hack to get the default value to be added to the config file.

    }

    public List<String> getWorldGuardPermissions() {
        String key = "settings.world-guard.blocked-regions.value";
        if (this.getStringList(key, null) == null || this.getStringList(key, null).isEmpty()) {
            this.setProperty(key, Arrays.asList("example1", "example2"));
        }
        return this.getStringList(key, null);

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
