package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import io.netty.buffer.Unpooled;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class GoToTalosWS extends TextWebSocketHandler {

    public static Map<Face, Map<String, WebSocketSession>> sessions = new HashMap<>();
    public static Map<Face, String> authorized = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, ParseException {
        String payload = message.getPayload();
        JSONObject json = (JSONObject) new JSONParser().parse(payload);
        String username = json.get("username").toString();
        String proxyId = json.get("proxyId").toString();
        if (!authorized.isEmpty()) {
            if (authorized.get(ApiRest.sessions.get(proxyId)) != null) {
                if(Objects.equals(authorized.get(ApiRest.sessions.get(proxyId)), username)) {
                    Map<String, WebSocketSession> values = new HashMap<>();
                    values.put(username, session);
                    sessions.put(ApiRest.sessions.get(proxyId), values);
                }
            }
        }

    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        List<Face> toRemove = new ArrayList<>();
        sessions.forEach((g, a)->{
            a.values().forEach((p)->{
                if (p == session) {
                    toRemove.add(g);
                }
            });
        });
        toRemove.forEach((a)->{
            ApiRest.sessions.remove(authorized.get(a));
            sessions.remove(a);
            authorized.remove(a);
        });


    }
    //Il faut envoyer depuis l'event face les packets a toutes les sessions websocket -> donc on envoie tout les packets de toutes les personnes connectés à un talos précis qui a été open par une requete POST a cette api

}
