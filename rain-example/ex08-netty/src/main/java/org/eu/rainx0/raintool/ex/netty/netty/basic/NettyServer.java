package org.eu.rainx0.raintool.ex.netty.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * @author xiaoyu
 * @time 2025/8/3 14:52
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup workers = new NioEventLoopGroup(10);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    // 两个线程组
                    .group(boss, workers)
                    //指定 server 用哪种 channel 实现
                    .channel(NioServerSocketChannel.class)
                    // 多个 client 同时连接, 将不能处理的 client 放入队列, 容量 1024
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 为 workers 中的 socket channel 设置处理器
                            socketChannel.pipeline().addLast(new NettyServerHandler());

                        }
                    });

            // 异步操作, 绑定端口
            ChannelFuture fu = bootstrap.bind(9090);
            //判断异步操作是否完成
//        boolean done = fu.isDone();

            // 等待异步操作执行完毕
            fu.sync();

            System.out.println("Server: started");

            // 关闭通道
            fu.channel().closeFuture().sync();

        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }



    static  class NettyServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client connected");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();//双向链接, 入站/出站

            ByteBuf buf = (ByteBuf) msg;
            System.out.println("Received: " + buf.toString(StandardCharsets.UTF_8));
        }
    }

}
