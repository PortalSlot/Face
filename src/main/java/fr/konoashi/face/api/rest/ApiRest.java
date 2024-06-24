package fr.konoashi.face.api.rest;

import com.google.gson.JsonArray;
import com.grack.nanojson.JsonObject;
import fr.konoashi.face.Face;
import fr.konoashi.face.api.Event;
import fr.konoashi.face.api.FaceApiController;
import fr.konoashi.face.event.EventManager;
import org.json.simple.JSONObject;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class ApiRest {

    @PostMapping(path = "/startFace")
    public String startProxyServer(@RequestBody JSONObject rawdata) throws IOException {

        Face face = new Face(Integer.parseInt(rawdata.get("listenPort").toString()), rawdata.get("proxyId").toString(), Integer.parseInt(rawdata.get("hubId").toString()), rawdata.get("motd").toString(), Integer.parseInt(rawdata.get("slots").toString()), rawdata.get("version").toString(), Integer.parseInt(rawdata.get("protocol").toString()));
        EventManager.register(new Event());
        new Thread(face::run).start();
        //Start a websocket and send the code to access this Face instance
        String proxyId = rawdata.get("proxyId").toString();
        FaceApiController.idFaceMap.put(proxyId, face);
        return proxyId;
    }

}
