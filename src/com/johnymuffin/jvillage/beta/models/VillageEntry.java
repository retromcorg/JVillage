package com.johnymuffin.jvillage.beta.models;

import java.util.Objects;
import java.util.UUID;

public class VillageEntry {
    private UUID uuid;
    private String villageName;


    public VillageEntry(UUID uuid, String villageName) {
        this.uuid = uuid;
        this.villageName = villageName;
    }


    public UUID getUUID() {
        return uuid;
    }

    public String getVillageName() {
        return villageName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VillageEntry)) return false;
        VillageEntry entry = (VillageEntry) obj;
        return this.uuid == entry.uuid && this.villageName.equalsIgnoreCase(entry.villageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, villageName);
    }

}
