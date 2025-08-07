package org.eu.rainx0.raintool.ex.mqtt.netty.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author xiaoyu
 * @time 2025/8/7 17:52
 */
public class ProtobufServer {
    private static final int port = 9527;
    private static final ServerBootstrap boot = new ServerBootstrap();
    private static final NioEventLoopGroup boss = new NioEventLoopGroup();
    private static final NioEventLoopGroup worker = new NioEventLoopGroup();

    public static void main(String[] args) throws InterruptedException {
        try {

            ChannelFuture fu = boot.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(UserProto.UserMsg.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new Handler())
                            ;
                        }
                    })
                    .bind(port).sync();
            System.out.println("Server started.");

            fu.channel().closeFuture().sync();

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    static class Handler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client connected, " + ctx.channel().remoteAddress());

            ctx.writeAndFlush(UserProto.UserMsg.newBuilder().setId(1).setAge(11).setState(0).setName("rain").build());

            super.channelActive(ctx);
        }
    }
}
