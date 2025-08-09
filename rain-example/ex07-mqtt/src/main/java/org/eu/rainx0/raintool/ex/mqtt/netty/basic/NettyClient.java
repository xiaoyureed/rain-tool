package org.eu.rainx0.raintool.ex.mqtt.netty.basic;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

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
                        //解码和编码，应和服务端一致
                        nioSocketChannel.pipeline()
                                // \n 和 \r\n , 基于分隔符的帧解码器
                                // 最大帧长度为 8192 字节
                                //自动将接收到的数据按行分割成独立的消息单元
                                // inbound handler
                                .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                                //将字节转换为字符串, inbound handler
                                .addLast("decoder", new StringDecoder())
                                //将字符串转换为字节, outbound handler
                                .addLast("encoder", new StringEncoder())
                                // inbound handler
                                .addLast("handler", new ClientHandler());

                    }
                });
        /**
         * 都会阻塞直到操作完成
         *
         * sync() 异常会被正常抛出
         * await()   不重新抛出异常
         *      需要通过ChannelFuture.isSuccess(), ChannelFuture.cause() 来获取异常
         */
        ChannelFuture fu = boot.connect("localhost", 9090).sync();
        System.out.println("connected to server");

        Channel ch = fu.channel();
        ch.writeAndFlush("client: Hello\n");

        ch.closeFuture().sync();// 阻塞, 等待 channel 关闭
        System.out.println("Client: channel closed"); // or 通过 addListener 监听 channel 关闭


    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {

        private ByteBuf buf;

        // 连接建立后触发
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = Unpooled.copiedBuffer("client: Conn established\n".getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(buf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf m = (ByteBuf) msg;
            buf.writeBytes(m);
            m.release();

//            if (buf.readableBytes() >= 4) { // 已经凑够4个byte，将4个byte组合称为一个int
//                long result = buf.readUnsignedInt();
//                ctx.close();
//            }

            System.out.println("Got: " + buf.toString(StandardCharsets.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(cause.getMessage());
            ctx.close(); // 关闭 channel
        }

        // handler 被添加时触发
        // 可用来做 Handler 的初始化
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            buf = ctx.alloc().buffer(4); // 4字节

            super.handlerAdded(ctx);
        }

        // handler 移除时触发, 可对缓冲区进行清理
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            buf.release();
            buf = null;
            super.handlerRemoved(ctx);
        }
    }
}
