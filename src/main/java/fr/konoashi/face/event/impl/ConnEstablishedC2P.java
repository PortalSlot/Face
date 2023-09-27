package fr.konoashi.face.event.impl;

import fr.konoashi.face.ClientConn;
import fr.konoashi.face.event.Event;
import io.netty.channel.Channel;

public class ConnEstablishedC2P extends Event {

        private String username;

        private ClientConn player;

        public String getUsername() {
            return username;
        }

    public ClientConn getPlayer() {
        return player;
    }

    public ConnEstablishedC2P(String username, ClientConn player) {
            this.player = player;
            this.username = username;
        }


}
