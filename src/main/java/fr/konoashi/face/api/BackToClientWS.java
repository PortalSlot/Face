package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import io.netty.buffer.Unpooled;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
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

        JSONObject requestJson = (org.json.simple.JSONObject) new JSONParser().parse(request);

        if (ApiRest.sessions.get(requestJson.get("proxyId").toString()) != null) {
            System.out.println(session.getId());
            Map<Face, String> value = new HashMap<>();
            value.put(ApiRest.sessions.get(requestJson.get("proxyId").toString()), requestJson.get("username").toString());
            sessions.put(session, value);
            authorized.add(session);
            session.sendMessage(new TextMessage("Success!"));
        } else {
            System.out.println("Invalid session ID!");
            session.sendMessage(new TextMessage("Invalid session ID!"));
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer request = message.getPayload();
        System.out.println("Send to client: " + Arrays.toString(request.array()));

        if (authorized.contains(session)) {
            for (int i = 0; i <sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().size() ; i++) {
                if(Objects.equals(sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().get(i).getUsername(), sessions.get(session).entrySet().iterator().next().getValue())) {
                    sessions.get(session).entrySet().iterator().next().getKey().getClientsConnectedOnProxy().get(i).sendToClient(Unpooled.copiedBuffer(request));
                }
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        authorized.remove(session);
    }

    //Il faut envoyer au bon client

}
