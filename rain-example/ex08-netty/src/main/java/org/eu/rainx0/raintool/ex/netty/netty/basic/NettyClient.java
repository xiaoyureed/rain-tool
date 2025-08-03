package org.eu.rainx0.raintool.ex.netty.netty.basic;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * @author xiaoyu
 * @time 2025/8/3 15:12
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap()
                .group(group)
                // 指定通道实现
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        ChannelFuture fu = boot.connect("localhost", 9090).sync();
        System.out.println("Client: connected to server");

        fu.channel().closeFuture().sync();
        System.out.println("Client: channel closed");


    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {
        // 连接建立后触发
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = Unpooled.copiedBuffer("hello world".getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(buf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("Got: " + buf.toString(StandardCharsets.UTF_8));
        }
    }
}
