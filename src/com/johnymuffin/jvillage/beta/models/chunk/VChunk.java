package com.johnymuffin.jvillage.beta.models.chunk;

import org.bukkit.Location;

import java.util.Objects;

public class VChunk {
    private String worldName;
    private int x;
    private int z;

    public VChunk(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public VChunk(Location location) {
        this(location.getWorld().getName(), location.getBlock().getChunk().getX(), location.getBlock().getChunk().getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VChunk)) return false;
        VChunk chunk = (VChunk) obj;
        return this.x == chunk.x && this.z == chunk.z && this.worldName.equalsIgnoreCase(chunk.worldName);
    }

    @Override
    public String toString() {
        return worldName + ": " + x + "," + z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldName.toLowerCase());
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
