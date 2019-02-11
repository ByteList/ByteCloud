package de.bytelist.bytecloud.packet;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by nemmerich on 11.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ByteCloudPacketServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connection to client is active.");
    }
}
