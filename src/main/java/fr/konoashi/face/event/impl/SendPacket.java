package fr.konoashi.face.event.impl;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import fr.konoashi.face.event.Event;

public class SendPacket extends Event {
    private ByteBuf buffer;

    private String username;

    private String ip;

    private Channel channel;

    private int id;

    public ByteBuf getBuffer() {
        return buffer;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public SendPacket(String username, String ip, ByteBuf buffer) {
        this.buffer = buffer;
        this.username = username;
        this.ip = ip;
    }

}
