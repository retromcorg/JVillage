package com.johnymuffin.jvillage.beta.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

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

    //Create from JSON
    public VCords(JSONObject jsonObject) {
        this.x = Long.valueOf(String.valueOf(jsonObject.get("x"))).intValue();
        this.y = Long.valueOf(String.valueOf(jsonObject.get("y"))).intValue();
        this.z = Long.valueOf(String.valueOf(jsonObject.get("z"))).intValue();
        this.worldName = (String) jsonObject.get("world");
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("z", z);
        jsonObject.put("world", worldName);
        return jsonObject;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VCords)) return false;
        VCords cords = (VCords) obj;
        return this.worldName == cords.worldName && this.x == cords.x && this.y == cords.y && this.z == cords.z;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z + " - " + worldName;
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
