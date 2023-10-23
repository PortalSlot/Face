package fr.konoashi.face;

import fr.konoashi.face.event.impl.SendPacket;
import fr.konoashi.face.network.ProtocolState;
import fr.konoashi.face.pipeline.NetworkEncryption;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.tinylog.Logger;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class ClientConn {
    private String username;
    private Channel clientChannel;
    private ProtocolState state = ProtocolState.HANDSHAKING;
    private Face proxyServer;

    public ClientConn(Channel clientChannel, Face proxyServer) {
        this.clientChannel = clientChannel;
        this.proxyServer = proxyServer;
    }

    public void disconnect() {
        this.disconnectServer();
        this.disconnectClient();
    }

    public void disconnectClient() {
        if(clientChannel != null) {
            try {
                clientChannel.close().sync();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void disconnectServer() {
        // Envoie d'un certain packet particulier pour annoncer la création d'un bot afk
    }

    private void closeChannel(Channel channel) {
        if(channel == null)
            return;

        try {
            channel.close().sync();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static final String ENCRYPTION_HANDLER_NAME = "encryption";

    public void enableEncryption(SecretKey sharedSecret) {
        try {
            clientChannel.pipeline().addBefore("sizer", ENCRYPTION_HANDLER_NAME, new NetworkEncryption(sharedSecret));
            Logger.info("Enabled encryption");
        } catch (GeneralSecurityException ex) {
            Logger.error(ex, "Failed to enable encryption");
        }
    }

    public void sendToClient(ByteBuf packet) {
        if(clientChannel.isWritable()) {
            try {
                ByteBuf mbuf = clientChannel.alloc().buffer();
                mbuf.writeBytes(packet.array());
                clientChannel.writeAndFlush(mbuf).sync();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void sendToServer(ByteBuf packet) {
        //Proxy ID used to recognize which proxy has been used
        new SendPacket(this.getUsername(), proxyServer.getProxyId(), packet, this.getProxyServer()).call();
    }

    /*public void setUsername(String username) {
        this.username = username;
        this.account = AuthenticationHandler.getInstance().getByUsername(username);
    }*/

    public Channel getClientChannel() {
        return clientChannel;
    }

    public ProtocolState getState() {
        return state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setState(ProtocolState state) {
        Logger.info("State transitioned: {}", state.name());
        this.state = state;
    }

    public Face getProxyServer() {
        return proxyServer;
    }
}
