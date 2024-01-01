package com.johnymuffin.jvillage.beta.routes.api.v1;

import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageFlags;
import com.johnymuffin.jvillage.beta.player.VPlayer;
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
import java.util.regex.Pattern;

public class JVillageGetPlayerRoute extends JVillageNormalRoute {

    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        if (request.getParameter("uuid") == null) {
            this.returnError(response, "No UUID field provided");
            return;
        }
        final String uuid = request.getParameter("uuid");
        Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        if (!p.matcher(uuid).matches()) {
            this.returnError(response, "Invalid UUID provided");
            return;
        }

        //Change to async
        final AsyncContext ctxt = request.startAsync();
        ctxt.start(() -> {
            //Change to Bukkit Synchronised Task
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.jVillage, () -> {
                try {
                    UUID playerUUID = UUID.fromString(uuid);
                    JSONObject playerJSON = new JSONObject();
                    if (!jVillage.getFundamentals().getPlayerMap().isPlayerKnown(playerUUID)) {
                        playerJSON.put("found", false);
                        playerJSON.put("error", false);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(playerJSON.toJSONString());
                        ctxt.complete();
                        return;
                    }
                    playerJSON.put("found", true);
                    VPlayer player = jVillage.getPlayerMap().getPlayer(playerUUID);

                    playerJSON.put("name", player.getUsername());
                    playerJSON.put("uuid", player.getUUID().toString());

                    JSONObject villageJSON = new JSONObject();

                    //Get all villages player owns
                    JSONArray villagesOwned = new JSONArray();
                    for (Village village : player.getTownsOwned()) {
                        villagesOwned.add(village.getTownUUID().toString());
                    }
                    villageJSON.put("owner", villagesOwned);

                    //Get all villages player assists
                    JSONArray villagesAssisted = new JSONArray();
                    for (Village village : player.getTownsAssistantOf()) {
                        villagesAssisted.add(village.getTownUUID().toString());
                    }
                    villageJSON.put("assistant", villagesAssisted);

                    //Get all villages player is a member of
                    JSONArray villagesMember = new JSONArray();
                    for (Village village : player.getTownsMemberOf()) {
                        villagesMember.add(village.getTownUUID().toString());
                    }
                    villageJSON.put("member", villagesMember);

                    playerJSON.put("villages", villageJSON);

                    playerJSON.put("error", false);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(playerJSON.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctxt.complete();
            }, 0L);
        });
    }

}
