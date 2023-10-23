package fr.konoashi.face;

import fr.konoashi.face.event.SubscribeEvent;
import fr.konoashi.face.event.impl.ConnEstablishedC2P;
import fr.konoashi.face.event.impl.SendPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Event {

    @SubscribeEvent
    public static void event(ConnEstablishedC2P e) {
        System.out.println("Established conn between client and proxy");
    }

    @SubscribeEvent
    public static void event(SendPacket e) {
        System.out.println("Send to server: " +  Arrays.toString(e.getBuffer().array()));
    }
}
