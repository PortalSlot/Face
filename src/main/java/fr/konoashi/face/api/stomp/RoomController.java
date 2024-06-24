package fr.konoashi.face.api.stomp;

import fr.konoashi.face.event.impl.RecievePacket;
import io.netty.buffer.ByteBuf;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.util.Arrays;


@org.springframework.stereotype.Controller
public class RoomController {

    @MessageMapping("/room/{roomId}")
    public void greeting(ByteBuf message, @DestinationVariable String roomId) throws Exception {

        new RecievePacket(roomId, message).call();
        System.out.println("Stomp recieved: " + Arrays.toString(message.array()));
        //MessageSender.sendMessage("/room/" +id, "d'accord mec");

    }

}
