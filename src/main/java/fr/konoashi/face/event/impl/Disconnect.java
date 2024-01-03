package fr.konoashi.face.event.impl;


import fr.konoashi.face.Face;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import fr.konoashi.face.event.Event;

public class Disconnect extends Event {
    private String string;

    private String username;

    private String ip;

    private Channel channel;

    private int id;

    private Face face;

    public String getString() {
        return string;
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

    public Disconnect(String username, String ip, String string, Face face) {
        this.string = string;
        this.username = username;
        this.ip = ip;
        this.face = face;
    }

    public Face getFace() {
        return face;
    }
}

