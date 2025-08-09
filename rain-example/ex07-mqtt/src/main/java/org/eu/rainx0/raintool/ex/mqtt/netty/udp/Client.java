package org.eu.rainx0.raintool.ex.mqtt.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author xiaoyu
 * @time 2025/8/8 14:18
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInboundHandlerAdapter() {
                })
                // 对于UDP client 来说，并不存在地址绑定一说，所以调用bind(0)。
                .bind(0).sync();

        System.out.println("Client started");

        fu.channel().closeFuture().sync();
    }
}
