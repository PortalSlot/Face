package fr.konoashi.face.network;

import fr.konoashi.face.util.Utils;

public enum ProtocolState {

    HANDSHAKING, STATUS, LOGIN, PLAY;

    public String getDisplayName() {
        return Utils.capitalize(this.name().toLowerCase());
    }

}
