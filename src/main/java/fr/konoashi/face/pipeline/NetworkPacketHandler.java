package fr.konoashi.face.pipeline;

import fr.konoashi.face.ClientConn;
import fr.konoashi.face.Face;
import fr.konoashi.face.event.impl.ConnEstablishedC2P;
import fr.konoashi.face.network.PacketBuffer;
import fr.konoashi.face.network.ProtocolState;
import fr.konoashi.face.util.AuthUtils;
import fr.konoashi.face.util.CryptUtil;
import fr.konoashi.face.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.UUID;

public class NetworkPacketHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ClientConn server;

    public NetworkPacketHandler(ClientConn server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        //Act as a server for the real client
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.duplicate().readBytes(bytes);
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(bytes);
        PacketBuffer packetBuffer = new PacketBuffer(copiedBuffer);
        System.out.println("Should be sent to server: " + Arrays.toString(copiedBuffer.array()));

        int packetId = packetBuffer.readVarIntFromBuffer();

        if (this.server.getState() == ProtocolState.PLAY) {
            this.server.sendToServer(copiedBuffer); //This method trigger an event, if there's any subscribe event to it, it's possible to get data and forward them
        } else if (this.server.getState() == ProtocolState.LOGIN) {
            if (packetId == 0x00) {
                String username = packetBuffer.readStringFromBuffer(16);
                server.setUsername(username);
                loginSuccess();
                new ConnEstablishedC2P(username, server, server.getProxyServer()).call(); //Conn between fake server and bot client must be init here
                this.server.setState(ProtocolState.PLAY);
                server.getProxyServer().getClientsConnectedOnProxy().add(server); //Conn established

            }
        } else if (this.server.getState() == ProtocolState.STATUS) {
            if (packetId == 0x00) {
                String icon = new String(Files.readAllBytes(Paths.get("icon.txt")));
                String motd = "\\\u00a7d            PortalSlot Proxy\\u00a7c [" + this.server.getProxyServer().getVersion() +"]\\u00a7r\\n\\u00a7b                Hub #"+String.format("%02d", this.server.getProxyServer().getHubId())+"\\u00a7c |\\u00a7e Id #"+ this.server.getProxyServer().getProxyId() + "\\u00a75";
                switch (this.server.getProxyServer().getMotd()) {
                    case "2b2t":
                        motd = "\\\u00a7d            PortalSlot Proxy\\u00a7c [" + this.server.getProxyServer().getVersion() +"]\\u00a7r\\n\\u00a7b                Hub #"+String.format("%02d", this.server.getProxyServer().getHubId())+"\\u00a7c |\\u00a7e Id #"+ this.server.getProxyServer().getProxyId() + "\\u00a75";
                    case "hypixel":
                        motd = "\\\u00a7d            PortalSlot Proxy\\u00a7c [" + this.server.getProxyServer().getVersion() +"]\\u00a7r\\n\\u00a7b                Hub #"+String.format("%02d", this.server.getProxyServer().getHubId())+"\\u00a7c |\\u00a7e Id #"+ this.server.getProxyServer().getProxyId() + "\\u00a75";
                }
                String serverInfo = "{\n" +
                        "    \"version\": {\n" +
                        "        \"name\": \"" + this.server.getProxyServer().getVersion() +"\",\n" +
                        "        \"protocol\": " + this.server.getProxyServer().getProtocol() +"\n" +
                        "    },\n" +
                        "    \"players\": {\n" +
                        "        \"max\": " + this.server.getProxyServer().getSlots()+",\n" +
                        "        \"online\": " + this.server.getProxyServer().getClientsConnectedOnProxy().size() +"\n" +
                        "    },\n" +
                        "    \"description\": {\n" +
                        "        \"text\": \""+ motd +"\"\n" +
                        "    },\n" +
                        "    \"favicon\": \""+ icon +"\"\n" +
                        "}";
                ByteBuf buf = Unpooled.buffer();
                PacketBuffer packet = new PacketBuffer(buf);
                packet.writeVarIntToBuffer(0);
                packet.writeString(serverInfo);
                server.sendToClient(Unpooled.copiedBuffer(Arrays.copyOfRange(packet.array(), 0, serverInfo.length()+4)));
            }
            if (packetId == 0x01) {
                ByteBuf buf = Unpooled.buffer();
                PacketBuffer packet = new PacketBuffer(buf);
                packet.writeVarIntToBuffer(1);
                packet.writeBytes(packetBuffer);
                this.server.sendToClient(Unpooled.copiedBuffer(Arrays.copyOfRange(packet.array(), 0, 9)));
                server.disconnectClient();
            }
        } else if (this.server.getState() == ProtocolState.HANDSHAKING) {
            if (packetId == 0x00) {
                int protocolVersion = packetBuffer.readVarIntFromBuffer();
                String ip = packetBuffer.readStringFromBuffer(250);
                boolean hasFMLMarker = ip.contains("\u0000FML\u0000");
                ip = ip.split("\u0000")[0];
                int port = packetBuffer.readUnsignedShort();
                int nextState = packetBuffer.readVarIntFromBuffer();
                if (nextState == 1) {
                    this.server.setState(ProtocolState.STATUS);
                }
                if (nextState == 2) {
                    this.server.setState(ProtocolState.LOGIN);
                }
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        server.getProxyServer().getClientsConnectedOnProxy().remove(server); //Conn removed
        this.server.disconnect();
    }

    public void loginSuccess() {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeVarIntToBuffer(2);
        packet.writeString("bacbf1c7-a48c-48d7-9bf3-1abc6e4d1330");
        packet.writeString(this.server.getUsername());
        this.server.sendToClient(Unpooled.copiedBuffer(Arrays.copyOfRange(packet.array(), 0, 3+"bacbf1c7-a48c-48d7-9bf3-1abc6e4d1330".length()+this.server.getUsername().length())));
    }

}
