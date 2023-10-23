package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import io.netty.buffer.Unpooled;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class BackToClientWS extends TextWebSocketHandler {

    public static Map<WebSocketSession, Map<Face, String>> sessions = new HashMap<>();
    public static List<WebSocketSession> authorized = new ArrayList<>();
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, ParseException {
        String request = message.getPayload();
        System.out.println(request);

        if (authorized.contains(session)) {
            for (int i = 0; i <sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().size() ; i++) {
                if(Objects.equals(sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().get(i).getUsername(), sessions.get(session).entrySet().iterator().next().getValue())) {
                    sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().get(i).sendToClient(Unpooled.copiedBuffer(StandardCharsets.US_ASCII.encode(message.getPayload()).array()));
                }
            }
        }
        JSONObject requestJson = (JSONObject) new JSONParser().parse(request);

        if (ApiRest.sessions.get(request) != null) {
            sessions.put(session, (Map<Face, String>) new HashMap<>().put(ApiRest.sessions.get(requestJson.getString("proxyId")), requestJson.getString("username")));
            authorized.add(session);
            session.sendMessage(new TextMessage("Success!"));
        } else {
            session.sendMessage(new TextMessage("Invalid session ID!"));
        }
    }
    //Il faut envoyer au bon client

}
