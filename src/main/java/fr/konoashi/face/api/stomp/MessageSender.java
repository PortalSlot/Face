package fr.konoashi.face.api.stomp;

import io.netty.buffer.ByteBuf;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;

public class MessageSender {

    public static void sendMessage(String destination, byte[] payload) {
        ApplicationContext context = ApplicationContextProvider.getContext();
        if (context != null) {
            SimpMessagingTemplate messagingTemplate = context.getBean(SimpMessagingTemplate.class);
            messagingTemplate.convertAndSend(destination, payload);
            System.out.println("Stomp sent: " + Arrays.toString(payload));
        } else {
            System.out.println("ApplicationContext is not available.");
        }
    }
}
