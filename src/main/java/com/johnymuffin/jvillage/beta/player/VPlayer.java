package com.johnymuffin.jvillage.beta.player;


import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.events.PlayerSwitchTownEvent;
import com.johnymuffin.jvillage.beta.models.Village;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.jvillage.beta.JVUtility.getPlayerFromUUID;

public class VPlayer {
    //Start - Village Memberships
    private ArrayList<Village> memberships = new ArrayList<>();
    //End - Village Memberships
    private UUID uuid;

    private JVillage plugin;

    private Village currentlyLocatedIn = null;

    private Village selectedVillage = null;

    private boolean isAutoClaimingEnabled = false;

    private boolean isAutoUnclaimingEnabled = false;

    private ArrayList<Village> invitedTo = new ArrayList<>();

    public VPlayer(JVillage plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;

        //Start - Village Memberships
        for (UUID villageUUID : plugin.getVillageMap().getKnownVillages()) {
            Village village = plugin.getVillageMap().getVillage(villageUUID);
            if (village.isMember(uuid)) {
                memberships.add(village);
            } else if (village.isInvited(uuid)) {
                invitedTo.add(village);
            }
        }
        //End - Village Memberships

        //Automatically select the first village in the list if they only reside in one village
        if (memberships.size() == 1) {
            selectedVillage = memberships.get(0);
        }

    }


    /**
     * @return The number of villages the player is a member of, but not the owner of.
     */
    public int getVillageMembershipCount() {
        int membershipCount = 0;
        for (Village village : memberships) {
            if (!village.isOwner(uuid)) {
                membershipCount++;
            }
        }
        return membershipCount;
    }

    /**
     * @return The number of villages the player is the owner of.
     */
    public int getVillageOwnershipCount() {
        int ownershipCount = 0;
        for (Village village : memberships) {
            if (village.isOwner(uuid)) {
                ownershipCount++;
            }
        }
        return ownershipCount;
    }

    public boolean isPlayerInVillage(Village village) {
        return isPlayerInVillage(village.getTownUUID());
    }

    public boolean isPlayerInVillage(UUID villageUUID) {
        for (Village village : memberships) {
            if (village.getTownUUID().equals(villageUUID)) {
                return true;
            }
        }
        return false;
    }

//    public String getUsername() {
//        String username = PoseidonUUID.getPlayerUsernameFromUUID(uuid);
//        if(username == null) {
//            username = this.plugin.getFundamentals().getPlayerCache().getUsernameFromUUID(uuid);
//        }
//        if(username == null) {
//            username = "Unknown Username";
//        }
//        return username;
//    }

    public boolean removeVillageMembership(Village village) {
        return memberships.remove(village);
    }

    public void inviteToVillage(Village village) {
        village.invitePlayer(uuid);
        invitedTo.add(village);
    }

    public boolean isInvitedToVillage(Village village) {
        return village.isInvited(uuid);
    }

    public ArrayList<Village> getInvitedToVillages() {
        return invitedTo;
    }

    public void removeInvitationToVillage(Village village) {
        village.uninvitePlayer(uuid);
        invitedTo.remove(village);
    }

    public boolean autoSwitchSelected() {
//        if (getFundamentalsPlayer().getInformation("jvillage.autoswitch") == null) {
//            return true;
//        }
//        return Boolean.parseBoolean(String.valueOf(getFundamentalsPlayer().getInformation("jvillage.autoswitch")));

        String autoSwitch = plugin.getPlayerData().getPlayerData(uuid, "autoswitch");
        if (autoSwitch == null) {
            plugin.debugLogger(Level.INFO, "AutoSwitch is null in players.yml for " + getUsername() + ". Assuming true.");
            return true;
        }
        return Boolean.parseBoolean(autoSwitch);
    }

    public void setAutoSwitchSelected(boolean autoSwitch) {
//        getFundamentalsPlayer().saveInformation("jvillage.autoswitch", autoSwitch);
        plugin.getPlayerData().setPlayerData(uuid, "autoswitch", String.valueOf(autoSwitch));
    }

    public Village getCurrentlyLocatedIn() {
        return currentlyLocatedIn;
    }

    public void setCurrentlyLocatedIn(Player player, Village newlyLocatedIn) {
        if (currentlyLocatedIn != newlyLocatedIn) {
            //Fire event when player switches towns
//            System.out.println("[Pre-Event] Player " + player.getName() + " has switched towns from " + (currentlyLocatedIn != null ? currentlyLocatedIn.getTownName() : "Wilderness") + " to " + (newlyLocatedIn != null ? newlyLocatedIn.getTownName() : "Wilderness"));
            PlayerSwitchTownEvent event = new PlayerSwitchTownEvent(player, currentlyLocatedIn, newlyLocatedIn);
            Bukkit.getPluginManager().callEvent(event);
            this.currentlyLocatedIn = newlyLocatedIn;
        }
    }

    public void setCurrentlyLocatedIn(Village newlyLocatedIn) {
        this.currentlyLocatedIn = newlyLocatedIn;
    }

    public boolean isLocatedInVillage() {
        return currentlyLocatedIn != null;
    }

    public boolean leaveVillage(Village village) {
        if (village.getOwner().equals(uuid)) {
            throw new IllegalArgumentException("Cannot leave a village you own");
        }
        if (village.removePlayerFromVillage(uuid)) {
            // Change selected village if the selected village is the one being left
            if (selectedVillage != null && selectedVillage.getTownUUID().equals(village.getTownUUID())) {
                selectedVillage = null;
            }

            // Remove from memberships
            memberships.remove(village);
            return true;
        }
        return false;
    }

    public String getUsername() {
        String username = PoseidonUUID.getPlayerUsernameFromUUID(uuid);
        if (username == null) {
            username = plugin.getPlayerData().getUsername(uuid);
        }
        if (username == null) {
            username = "Unknown UUID";
        }
        return username;
    }


    public ArrayList<Village> getAllMemberships() {
        return memberships;
    }

    public Village[] getTownsOwned() {
        ArrayList<Village> townsOwned = new ArrayList<>();
        for (Village village : memberships) {
            if (village.isOwner(uuid)) {
                townsOwned.add(village);
            }
        }
        return townsOwned.toArray(new Village[0]);
    }

    public Village[] getTownsAssistantOf() {
        ArrayList<Village> townsAssistantOf = new ArrayList<>();
        for (Village village : memberships) {
            if (village.isAssistant(uuid) && !village.isOwner(uuid)) {
                townsAssistantOf.add(village);
            }
        }
        return townsAssistantOf.toArray(new Village[0]);
    }

    public Village[] getTownsMemberOf() {
        ArrayList<Village> townsMemberOf = new ArrayList<>();
        for (Village village : memberships) {
            if (!village.isAssistant(uuid) && !village.isOwner(uuid)) {
                townsMemberOf.add(village);
            }
        }
        return townsMemberOf.toArray(new Village[0]);
    }

    public UUID getUUID() {
        return uuid;
    }

    public Village getSelectedVillage() {
        return selectedVillage;
    }

    public void setSelectedVillage(Village selectedVillage) {
        //Disable auto claim
        if (this.isAutoClaimingEnabled) {
            this.isAutoClaimingEnabled = false;

            String message = this.plugin.getLanguage().getMessage("autoswitch_selected_village_disabled");
            message = message.replace("%village%", this.selectedVillage != null ? this.selectedVillage.getTownName() : "Wilderness");
            this.sendMessage(message);
        }

        this.selectedVillage = selectedVillage;
    }

    public void joinVillage(Village village) {
        memberships.add(village);
        village.addMember(uuid);
    }

    public boolean isPlayerOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean sendMessage(String message) {
        if (this.isPlayerOnline()) {
            Player player = getPlayerFromUUID(uuid);
            player.sendMessage(message);
            return true;
        }
        return false;
    }

    public boolean isAutoClaimingEnabled() {
        return this.isAutoClaimingEnabled;
    }

    public boolean isAutoUnclaimingEnabled() {
        return this.isAutoUnclaimingEnabled;
    }

    public void setAutoClaimingEnabled(boolean autoClaimingEnabled, boolean sendMessage) {
        if (this.isAutoClaimingEnabled == autoClaimingEnabled) {
            return;
        }


        this.isAutoClaimingEnabled = autoClaimingEnabled;
        if (!this.isAutoClaimingEnabled && sendMessage) {
            String message = this.plugin.getLanguage().getMessage("autoclaim_disabled");
            this.sendMessage(message);
        }
    }

    public void setAutoUnclaimingEnabled(boolean autoUnclaimingEnabled, boolean sendMessage) {
        if (this.isAutoUnclaimingEnabled == autoUnclaimingEnabled) {
            return;
        }


        this.isAutoUnclaimingEnabled = autoUnclaimingEnabled;
        if (!this.isAutoUnclaimingEnabled && sendMessage) {
            String message = this.plugin.getLanguage().getMessage("autounclaim_disabled");
            this.sendMessage(message);
        }
    }
}
