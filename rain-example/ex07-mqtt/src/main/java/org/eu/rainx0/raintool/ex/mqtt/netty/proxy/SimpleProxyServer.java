package org.eu.rainx0.raintool.ex.mqtt.netty.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;

/// [...](http://www.flydean.com/38-netty-cust-port-unification)
///
///
/// @author xiaoyu
/// @time 2025/8/8 16:53
public class SimpleProxyServer {
    private static final NioEventLoopGroup boss = new NioEventLoopGroup(1);
    private static final NioEventLoopGroup worker = new NioEventLoopGroup();
    public static void main(String[] args) throws InterruptedException {
        try {
            ServerBootstrap boot = new ServerBootstrap().group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new InboundHandler("", 0))

                                    ;
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, false);

            ChannelFuture fu = boot.bind(9527).sync();

            System.out.println("Server started.");

            fu.channel().closeFuture().sync();


        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @RequiredArgsConstructor
    static class InboundHandler extends ChannelInboundHandlerAdapter {

        private final String remoteHost;
        private final int remotePort;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel inboundChannel = ctx.channel();
            //ctx.pipeline();

            Bootstrap boot = new Bootstrap().group(inboundChannel.eventLoop())
                    .channel(inboundChannel.getClass())
                    .handler(new OutboundHandler(inboundChannel))
                    .option(ChannelOption.AUTO_READ, false);

            ChannelFuture fu = boot.connect(remoteHost, remotePort);

            super.channelActive(ctx);
        }
    }

    @RequiredArgsConstructor
    static class OutboundHandler extends ChannelOutboundHandlerAdapter {
        private final Channel inbound;


    }
}
