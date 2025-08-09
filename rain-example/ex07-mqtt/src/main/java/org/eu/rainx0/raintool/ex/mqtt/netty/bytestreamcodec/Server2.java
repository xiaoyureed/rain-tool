package org.eu.rainx0.raintool.ex.mqtt.netty.bytestreamcodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author xiaoyu
 * @time 2025/8/8 13:08
 */
public class Server2 {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new ServerBootstrap().group(new NioEventLoopGroup(1), new NioEventLoopGroup(10))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<ServerChannel>() {

                    @Override
                    protected void initChannel(ServerChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new IntDecoder())
                                .addLast("handler", new Handler())

                        ;
                    }
                })
                .bind(9527).sync();

        System.out.println("Server started.");

        fu.channel().closeFuture().sync();
    }

    static class IntDecoder extends ByteToMessageDecoder {

        /**
         *
         * 输入不是一个byte一个byte来的吗？为什么这里可以一次读取到4个byte？
         * 这是因为ByteToMessageDecoder内置了一个缓存装置，所以这里的in实际上是一个缓存集合。
         */
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            // 数据不够，等待更多数据
            if (in.readableBytes() < 4) {
                return;
            }

            //out.add(in.readBytes(in.readableBytes()));

            // 读取整数并添加到输出列表
            out.add(in.readInt());
            // 不需要释放 in,  in 是框架提供的资源, 会帮我们管理
            // 这里释放了, 会造成双重释放的异常
            //in.release()
        }
    }

    /**
     * 要接受 client 的一个 int 数字, 大小 4字节
     */
    static class Handler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            int m = (int) msg;
            System.out.println("收到: " + m);
        }
    }


}
