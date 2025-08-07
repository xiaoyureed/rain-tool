package org.eu.rainx0.raintool.ex.mqtt.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
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
                    //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    // 多个 client 同时连接, 将不能处理的 client 放入队列, 容量 1024
                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .localAddress(new InetSocketAddress(9090))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 为 workers 中的 socket channel 设置处理器
                            socketChannel.pipeline()
                                    // 以("\n")为结尾分割的 解码器
                                    .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                                    .addLast("decoder", new StringDecoder())
                                    .addLast("encoder", new StringEncoder())
                                    .addLast("handler", new NettyServerHandler());

                        }
                    })
                    //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //TCP延迟传输: 将小的数据包包装成更大的帧进行传送，提高网络的负载
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    ;

            // 异步操作, 绑定端口
            ChannelFuture fu = bootstrap.bind(9090);
            //判断异步操作是否完成
//        boolean done = fu.isDone();

            // 等待异步操作执行完毕
            fu.sync();

            System.out.println("Server: started");

            // 阻塞 main thread, 等待通道关闭
            fu.channel().closeFuture().sync();

        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }



    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client connected, " + ctx.channel().remoteAddress());
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();//双向链接, 入站/出站


//            ByteBuf bf = (ByteBuf) msg;   // 有 StringDecoder, 这里的 msg 是 String 对象, 不是字节了

            String buf = (String) msg;
            System.out.println("Got: " + buf);
        }
    }

}
