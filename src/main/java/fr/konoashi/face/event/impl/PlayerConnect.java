package fr.konoashi.face.event.impl;

import fr.konoashi.face.ClientConn;
import fr.konoashi.face.Face;
import fr.konoashi.face.event.Event;

public class PlayerConnect extends Event {

        private String username;

        private ClientConn player;

        private Face face;

        public String getUsername() {
            return username;
        }

    public ClientConn getPlayer() {
        return player;
    }

    public PlayerConnect(String username, ClientConn player, Face face) {
            this.player = player;
            this.username = username;
            this.face = face;
        }

    public Face getFace() {
        return face;
    }
}
