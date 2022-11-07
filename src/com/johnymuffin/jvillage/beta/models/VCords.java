package com.johnymuffin.jvillage.beta.models;

import org.bukkit.Location;

import java.util.Objects;

public class VCords {
    private int x;
    private int y;
    private int z;
    private String worldName;


    public VCords(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public VCords(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VCords)) return false;
        VCords cords = (VCords) obj;
        return this.worldName == cords.worldName && this.x == cords.x && this.y == cords.y && this.z == cords.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldName);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }
}
