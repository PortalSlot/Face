package fr.konoashi.face.api;

import fr.konoashi.face.Face;
import fr.konoashi.face.api.stomp.MessageSender;
import fr.konoashi.face.event.SubscribeEvent;
import fr.konoashi.face.event.impl.PlayerConnect;
import fr.konoashi.face.event.impl.PlayerDisconnect;
import fr.konoashi.face.event.impl.RecievePacket;
import fr.konoashi.face.event.impl.SendPacket;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.*;


public class Event {

    @SubscribeEvent
    public static void event(PlayerConnect e) throws IOException {
        System.out.println("Established conn between client and proxy");
        String roomId = UUID.randomUUID().toString();
        Map<String, String> usernameRoomdIdMap = new HashMap<>();
        usernameRoomdIdMap.put(e.getUsername(), roomId);
        FaceApiController.playerSpecificFaceRoomIdMap.put(e.getFace().getProxyId(), usernameRoomdIdMap);
        FaceApiController.clientStatusWebsocketList.forEach(s -> {
            String message = "{\n" +
                    "  \"status\": 200,\n" +
                    "  \"content\": {\n" +
                    "    \"faceId\": \"" + e.getFace().getProxyId() + "\",\n" +
                    "    \"event\": \"logIn\",\n" +
                    "    \"username\": \"" + e.getUsername() + "\",\n" +
                    "    \"roomId\": \"" + roomId + "\",\n" +
                    "  }\n" +
                    "}";
            try {
                s.sendMessage(new TextMessage(message));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    @SubscribeEvent
    public static void event(SendPacket e) throws IOException {
        MessageSender.sendMessage("/room/" + FaceApiController.playerSpecificFaceRoomIdMap.get(e.getFace().getProxyId()).get(e.getUsername()), e.getBuffer().array());
        System.out.println("Send to talos: " +  Arrays.toString(e.getBuffer().array()));
    }

    @SubscribeEvent
    public static void event(RecievePacket e) throws IOException {
        String faceId = getFaceIdByValue(FaceApiController.playerSpecificFaceRoomIdMap, e.getRoomId());
        String username = getUsernameByValue(FaceApiController.playerSpecificFaceRoomIdMap, e.getRoomId());
        Face face = FaceApiController.idFaceMap.get(faceId);

        face.getClientsConnectedOnProxy().forEach(a -> {
            if (Objects.equals(a.getUsername(), username)) {
                a.sendToClient(e.getBuffer());
            }
        });

    }

    @SubscribeEvent
    public static void event(PlayerDisconnect e) throws IOException {

        String roomId = FaceApiController.playerSpecificFaceRoomIdMap.get(e.getFace().getProxyId()).get(e.getUsername());
        deleteElement(FaceApiController.playerSpecificFaceRoomIdMap, e.getFace().getProxyId(), e.getUsername());

        FaceApiController.clientStatusWebsocketList.forEach(s -> {
            String message = "{\n" +
                    "  \"status\": 200,\n" +
                    "  \"content\": {\n" +
                    "    \"faceId\": \"" + e.getFace().getProxyId() + "\",\n" +
                    "    \"event\": \"logOut\",\n" +
                    "    \"username\": \"" + e.getUsername() + "\",\n" +
                    "    \"roomId\": \"" + roomId + "\",\n" +
                    "  }\n" +
                    "}";
            try {
                s.sendMessage(new TextMessage(message));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    public static void deleteElement(Map<String, Map<String, String>> map, String faceId, String username) {
        Map<String, String> faceIdMap = map.get(faceId);
        if (faceIdMap != null) {
            faceIdMap.remove(username);
            if (faceIdMap.isEmpty()) {
                map.remove(faceId);
            }
        }
    }

    public static String getFaceIdByValue(Map<String, Map<String, String>> map, String valueToFind) {
        // Itérer sur les entrées de la Map externe
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            // Obtenir la sous-map associée à la clé externe
            Map<String, String> subMap = entry.getValue();

            // Itérer sur les entrées de la sous-map
            for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                // Vérifier si la valeur spécifiée est égale à la valeur de la sous-map
                if (valueToFind.equals(subEntry.getValue())) {
                    // Si la valeur est trouvée, retourner la clé externe (playerId)
                    return entry.getKey();
                }
            }
        }

        // Si la valeur n'est pas trouvée, retourner null ou une valeur par défaut selon vos besoins
        return null;
    }

    public static String getUsernameByValue(Map<String, Map<String, String>> map, String valueToFind) {
        // Itérer sur les entrées de la Map externe
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            // Obtenir la sous-map associée à la clé externe
            Map<String, String> subMap = entry.getValue();

            // Itérer sur les entrées de la sous-map
            for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                // Vérifier si la valeur spécifiée est égale à la valeur de la sous-map
                if (valueToFind.equals(subEntry.getValue())) {
                    // Si la valeur est trouvée, retourner la clé externe (playerId)
                    return subEntry.getKey();
                }
            }
        }

        // Si la valeur n'est pas trouvée, retourner null ou une valeur par défaut selon vos besoins
        return null;
    }

}
