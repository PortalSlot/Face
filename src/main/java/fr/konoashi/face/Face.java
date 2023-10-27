package fr.konoashi.face;

import fr.konoashi.face.event.impl.SendPacket;
import fr.konoashi.face.network.ProtocolState;
import fr.konoashi.face.pipeline.NetworkEncryption;
import fr.konoashi.face.pipeline.NetworkPacketHandler;
import fr.konoashi.face.pipeline.NetworkPacketSizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.tinylog.Logger;

import javax.crypto.SecretKey;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Face {
        private final int listenPort;
        private EventLoopGroup group;
        private String proxyId;
        private int hubId;

        private int slots;

        private String motd;

        private String version;

        private int protocol;
        private List<ClientConn> clientsConnectedOnProxy = new ArrayList<>();

        static final EventExecutorGroup group2 = new DefaultEventExecutorGroup(16);

        public Face(int listenPort, String proxyId, int hubId, String motd, int slots, String version, int protocol) {
            this.listenPort = listenPort;
            this.proxyId = proxyId;
            this.hubId = hubId;
            this.motd = motd;
            this.slots = slots;
            this.version = version;
            this.protocol = protocol;
        }


        public void run() {
            Logger.info("Starting proxy...");

            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                ClientConn player = new ClientConn(ch, Face.this);
                                System.out.println("New Channel: " + ch.toString());
                                p.addLast("sizer", new NetworkPacketSizer());
                                //p.addLast("codec", new NetworkPacketCodec()); to use when multiple versions of the protocol will be supported
                                p.addLast("handler", new NetworkPacketHandler(player));
                            }
                        });

                // Start the server.
                ChannelFuture f = b.bind(this.listenPort).sync();

                // Wait until the server socket is closed.
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Shut down all event loops to terminate all threads.
                System.out.println("Server is closed");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }

    public String getProxyId() {
        return proxyId;
    }

    public int getListenPort() {
        return listenPort;
    }

    public EventLoopGroup getGroup() {
        return group;
    }

    public int getHubId() {
        return hubId;
    }

    public int getSlots() {
        return slots;
    }

    public String getMotd() {
        return motd;
    }

    public String getVersion() {
        return version;
    }

    public int getProtocol() {
        return protocol;
    }

    public List<ClientConn> getClientsConnectedOnProxy() {
        return clientsConnectedOnProxy;
    }
}
