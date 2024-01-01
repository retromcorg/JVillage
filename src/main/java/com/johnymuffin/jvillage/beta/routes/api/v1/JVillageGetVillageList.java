package com.johnymuffin.jvillage.beta.routes.api.v1;

import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.routes.JVillageNormalRoute;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class JVillageGetVillageList extends JVillageNormalRoute {

    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        //Change to async
        final AsyncContext ctxt = request.startAsync();
        ctxt.start(() -> {
            //Change to Bukkit Synchronised Task
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.jVillage, () -> {
                try {
                    JSONArray villageList = new JSONArray();
                    //Loop through all villages
                    for (UUID village : this.jVillage.getVillageMap().getKnownVillages()) {
                        Village villageObject = this.jVillage.getVillageMap().getVillage(village);
                        JSONObject villageJSON = new JSONObject();
                        villageJSON.put("name", villageObject.getTownName());
                        villageJSON.put("uuid", villageObject.getTownUUID().toString());
                        villageJSON.put("owner", villageObject.getOwner().toString());
                        villageList.add(villageJSON);
                    }
                    JSONObject responseObject = new JSONObject();
                    responseObject.put("error", false);
                    responseObject.put("villages", villageList);

                    //Send response
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().println(responseObject.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctxt.complete();
            }, 0L);
        });
    }


}
