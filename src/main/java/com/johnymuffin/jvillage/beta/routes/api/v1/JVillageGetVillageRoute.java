package com.johnymuffin.jvillage.beta.routes.api.v1;

import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.VillageFlags;
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

public class JVillageGetVillageRoute extends JVillageNormalRoute {

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

                    UUID villageUUID = UUID.fromString(uuid);

                    Village village = this.jVillage.getVillageMap().getVillage(villageUUID);

                    JSONObject villageJSON = new JSONObject();

                    //Return error if village does not exist
                    if (village == null) {
                        villageJSON.put("found", false);
                        villageJSON.put("error", false);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(villageJSON.toJSONString());
                        ctxt.complete();
                        return;
                    }

                    villageJSON.put("found", true);

                    villageJSON.put("name", village.getTownName());
                    villageJSON.put("uuid", village.getTownUUID().toString());
                    villageJSON.put("owner", village.getOwner().toString());
                    //Get all assistants
                    JSONArray assistants = new JSONArray();
                    for (UUID assistant : village.getAssistants()) {
                        assistants.add(assistant.toString());
                    }
                    villageJSON.put("assistants", assistants);
                    //Get all members
                    JSONArray members = new JSONArray();
                    for (UUID member : village.getMembers()) {
                        members.add(member.toString());
                    }
                    villageJSON.put("members", members);

                    villageJSON.put("spawn", village.getTownSpawn().getJsonObject());
                    //Town Flags
                    JSONObject flags = new JSONObject();
                    for (VillageFlags flag : village.getFlags().keySet()) {
                        flags.put(flag.toString(), village.getFlags().get(flag));
                    }
                    villageJSON.put("flags", flags);
                    villageJSON.put("claims", village.getTotalClaims());
                    villageJSON.put("error", false);

                    villageJSON.put("creationTime", village.getCreationTime());

                    villageJSON.put("balance", village.getBalance());

                    //Send response
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(villageJSON.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctxt.complete();
            }, 0L);
        });
    }

}
