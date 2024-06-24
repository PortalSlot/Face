package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceApiController {
    public static Map<String, Face> idFaceMap = new HashMap<>();
    public static List<WebSocketSession> clientStatusWebsocketList = new ArrayList<>(); //One per service trying to reach out face

    public static Map<String, Map<String, String>> playerSpecificFaceRoomIdMap = new HashMap<>();
}
