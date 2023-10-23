package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import io.netty.buffer.ByteBuf;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@Controller
public class GoToServerWS extends TextWebSocketHandler {

    public static Map<Face, WebSocketSession> sessions = new HashMap<>();
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String request = message.getPayload();
        System.out.println(request);

        if (ApiRest.sessions.get(request) != null) {
            sessions.put(ApiRest.sessions.get(request), session);
            session.sendMessage(new TextMessage("Success!"));
        } else {
            session.sendMessage(new TextMessage("Invalid session ID!"));
        }
    }
    //Il faut envoyer depuis l'event face les packets a toutes les sessions websocket -> donc on envoie tout les packets de toutes les personnes connectés à un talos précis qui a été open par une requete POST a cette api

}
