package com.johnymuffin.jvillage.beta.models;

import com.johnymuffin.jvillage.beta.JVUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

public class VSpawnCords extends VCords{

    private int yaw;

    public VSpawnCords(int x, int y, int z, int yaw, String worldName){
        super(x, y, z, worldName);
        this.yaw = yaw;
    }

    public VSpawnCords(Location location){
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), JVUtility.closestYaw(location.getYaw()), location.getWorld().getName());
    }

    public VSpawnCords(JSONObject jsonObject){
        super(Long.valueOf(String.valueOf(jsonObject.get("x"))).intValue(),
                Long.valueOf(String.valueOf(jsonObject.get("y"))).intValue(),
                Long.valueOf(String.valueOf(jsonObject.get("z"))).intValue(),
                (String) jsonObject.get("world"));
        if(jsonObject.get("yaw") == null){
            yaw = 0;
        }else{
            yaw = Long.valueOf(String.valueOf(jsonObject.get("yaw"))).intValue();
        }
    }

    @Override
    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", super.getX());
        jsonObject.put("y", super.getY());
        jsonObject.put("z", super.getZ());
        jsonObject.put("yaw", yaw);
        jsonObject.put("world", super.getWorldName());
        return jsonObject;
    }

    public int getYaw(){
        return yaw;
    }

    @Override
    public Location getLocation() {
        return new Location(Bukkit.getWorld(super.getWorldName()), super.getX(), super.getY(), super.getZ(), yaw, 0);
    }
}
