package fr.konoashi.face.api;

import fr.konoashi.face.event.SubscribeEvent;
import fr.konoashi.face.event.impl.ConnEstablishedC2P;
import fr.konoashi.face.event.impl.SendPacket;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Arrays;

public class Event {

    @SubscribeEvent
    public static void event(ConnEstablishedC2P e) throws IOException {
        System.out.println("Established conn between client and proxy");
        GoToServerWS.sessions.get(e.getFace()).sendMessage(new TextMessage("Established connexion between Client and Face"));
    }

    @SubscribeEvent
    public static void event(SendPacket e) throws IOException {
        GoToServerWS.sessions.get(e.getFace()).sendMessage(new BinaryMessage(e.getBuffer().array()));
        System.out.println("Send to server: " +  Arrays.toString(e.getBuffer().array()));
    }

}
