package fr.konoashi.face.api.websocket;

import fr.konoashi.face.Face;
import fr.konoashi.face.api.FaceApiController;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Controller
public class NewPlayerLoggedInFace extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String request = message.getPayload();
        System.out.println(request);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("added session");
        FaceApiController.clientStatusWebsocketList.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("removed session");
        FaceApiController.clientStatusWebsocketList.remove(session);
    }


}
