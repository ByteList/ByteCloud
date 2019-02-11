package de.bytelist.bytecloud.packet;

import com.sun.scenario.Settings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * Created by nemmerich on 11.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ByteCloudPacketClient {

    private final String address = "127.0.0.1";
    private final int port;
    private Charset charset;
    private EventLoopGroup workerGroup;

    public ByteCloudPacketClient(int port) {
        this.port = port;
        this.charset = Charset.forName("UTF-8");
    }

    public void connect() {
        this.workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(this.workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    System.out.println("Connected to packet-server: "+address+":"+port);
                    channel.pipeline().addLast(
                            new StringEncoder(charset),
                            new Line
                            new ByteCloudPacketClientHandler());
                }
            });
            bootstrap.connect(this.address, this.port).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.disconnect();
        }
    }

    public void disconnect() {
        this.workerGroup.shutdownGracefully();
    }

}
