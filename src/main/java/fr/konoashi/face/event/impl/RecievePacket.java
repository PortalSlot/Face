package fr.konoashi.face.event.impl;

import fr.konoashi.face.Face;
import fr.konoashi.face.event.Event;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class RecievePacket extends Event {
    private ByteBuf buffer;

    private String roomId;


    public ByteBuf getBuffer() {
        return buffer;
    }

    public String getRoomId() {
        return roomId;
    }


    public RecievePacket(String roomId, ByteBuf buffer) {
        this.buffer = buffer;
        this.roomId = roomId;
    }


}
