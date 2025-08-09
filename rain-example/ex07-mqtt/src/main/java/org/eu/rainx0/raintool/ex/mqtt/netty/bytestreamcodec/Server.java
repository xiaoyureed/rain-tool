package org.eu.rainx0.raintool.ex.mqtt.netty.bytestreamcodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author xiaoyu
 * @time 2025/8/8 13:08
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new ServerBootstrap().group(new NioEventLoopGroup(1), new NioEventLoopGroup(10))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<ServerChannel>() {

                    @Override
                    protected void initChannel(ServerChannel ch) throws Exception {
                        ch.pipeline().addLast(new Handler())

                        ;
                    }
                })
                .bind(9527).sync();

        System.out.println("Server started.");

        fu.channel().closeFuture().sync();
    }

    /**
     * 要接受 client 的一个 int 数字, 大小 4字节
     */
    static class Handler extends ChannelInboundHandlerAdapter {
        private ByteBuf buf;

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            buf = ctx.alloc().buffer(4);
            super.handlerAdded(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            buf.release();
            buf = null;
            super.handlerRemoved(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            ByteBuf m = (ByteBuf) msg;

            buf.readBytes(m);
            m.release();

            if (buf.readableBytes() >= 4) { // 已经凑够4个byte，将4个byte组合称为一个int
                long result = buf.readUnsignedInt();
                ctx.close();
            }

        }
    }

}
