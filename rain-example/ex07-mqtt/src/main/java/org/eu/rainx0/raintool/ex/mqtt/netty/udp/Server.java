package org.eu.rainx0.raintool.ex.mqtt.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author xiaoyu
 * @time 2025/8/8 14:14
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new Bootstrap() // server 端也使用 bootStrap
                .group(new NioEventLoopGroup())
                // udp Channel
                .channel(NioDatagramChannel.class)
                // 必须, 因为UDP是以广播的形式发送消息的
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new UdpServerHandler());
                    }
                })
                .bind(9527).sync();


        System.out.println("Server started.");

        fu.channel().closeFuture().sync();
    }

    static class UdpServerHandler extends ChannelInboundHandlerAdapter {
    }
}
