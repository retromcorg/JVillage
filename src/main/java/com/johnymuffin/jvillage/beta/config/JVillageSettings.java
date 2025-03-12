package com.johnymuffin.jvillage.beta.config;

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

        //Maximum claim size for /village claim rect and /village claim circle
//        generateConfigOption("settings.town-claim.maximum-claim-size.circle.enabled", true);
//        generateConfigOption("settings.town-claim.maximum-claim-size.circle.value", 6);
//        generateConfigOption("settings.town-claim.maximum-claim-size.circle.info", "The maximum chunk radius of a circle claiming permitted.");

        generateConfigOption("settings.town-claim.maximum-claim-size.rect.enabled", true);
        generateConfigOption("settings.town-claim.maximum-claim-size.rect.value", 6);
        generateConfigOption("settings.town-claim.maximum-claim-size.rect.info", "The maximum chunk radius of a rectangle claiming permitted.");

        generateConfigOption("settings.auto-claim.timer", 2);
        generateConfigOption("settings.auto-claim.info", "The amount of time in seconds between checks for auto-claiming.");

        generateConfigOption("settings.always-use-default-lang.info", "If true, the default language file will always be used. The plugin will ignore the language file.");
        generateConfigOption("settings.always-use-default-lang.enabled", false);

        generateConfigOption("settings.debug-mode.info", "If true, the plugin will output debug messages to the console.");
        generateConfigOption("settings.debug-mode.enabled", false);

        generateConfigOption("settings.import-placeholder-account.uuid", "f84c6a79-0a4e-45e0-879b-cd49ebd4c4e2");
        generateConfigOption("settings.import-placeholder-account.name", "Herobrine");
        generateConfigOption("settings.import-placeholder-account.info", "This is the account that will be given ownership of Villages imported from Towny and Factions that do not have a valid owner. This should be a VALID Mojang account.");


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
