package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Controller
public class NewPlayerLoggedInFace extends TextWebSocketHandler {

    public static Map<Face, WebSocketSession> sessions = new HashMap<>();

    public static Map<WebSocketSession, Face> sessionsBackward = new HashMap<>();
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String request = message.getPayload();
        System.out.println(request);

        if (ApiRest.sessions.get(request) != null) {
            sessions.put(ApiRest.sessions.get(request), session);
            sessionsBackward.put(session, ApiRest.sessions.get(request));
            session.sendMessage(new TextMessage("Success!"));
        } else {
            session.sendMessage(new TextMessage("Invalid session ID!"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(sessionsBackward.get(session));
        sessionsBackward.remove(session);

    }
    //Il faut envoyer depuis l'event face les packets a toutes les sessions websocket -> donc on envoie tout les packets de toutes les personnes connectés à un talos précis qui a été open par une requete POST a cette api

}
