package de.bytelist.bytecloud.packet;

import com.sun.scenario.Settings;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.Cloud;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;

/**
 * Created by nemmerich on 11.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ByteCloudPacketServer {

    private final int port;
    private Charset charset;
    private EventLoopGroup bossGroup, workerGroup;

    public ByteCloudPacketServer(int port) {
        this.port = port;
        this.charset = Charset.forName("UTF-8");
    }

    public void start() {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    System.out.println("Client connected with the following address: " + channel.remoteAddress().getHostName());
                    channel.pipeline().addLast(new ByteCloudPacketServerHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 50);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(this.port).sync();

            ByteCloud.getInstance().getLogger().info("Packet-Server started at port "+port+".");

            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.stop();
        }
    }

    public void stop() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
