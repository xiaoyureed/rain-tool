package org.eu.rainx0.raintool.ex.mqtt.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 * @time 2025/8/7 12:50
 */
public class HeartClient {
    private static final Bootstrap boot = new Bootstrap();
    private static final NioEventLoopGroup group = new NioEventLoopGroup();
    private static final String server_host = "127.0.0.1";
    private static final int server_port = 9527;

    public static void main(String[] args) throws InterruptedException {
        boot.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                /*
                                IdleStateHandler 会监控通道的读写活动
                                当超过设定时间没有相应操作时，会触发 IdleStateEvent 事件
                                触发的 IdleStateEvent 可以在 userEventTriggered() 方法中处理, 如发送心跳包, 或者传递给后续处理器

                                * 可以监控三种类型的空闲状态
                                * READER_IDLE：读空闲（一段时间内没有数据读取）
                                    WRITER_IDLE：写空闲（一段时间内没有数据写入）
                                    ALL_IDLE：总体空闲（一段时间内没有读写操作
                                *
                                * 构造参数:
                                * 第1个参数（0）：读空闲超时时间（秒）- 0表示不检测
                                * 第2个参数（4）：写空闲超时时间（秒）- 4秒没有写操作则触发事件
                                *   因为服务端设置的超时时间是5秒，所以设置4秒 (客户端心跳间隔应小于服务端超时间隔)
                                * 第3个参数（0）：总体空闲超时时间（秒）- 0表示不检测
                                * 第4个参数：时间单位
                                *
                                *
                                *
                                * */
                                .addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS))
                                .addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder())
                                .addLast("handler", new Handler());
                    }
                });
        ChannelFuture fu = boot.connect(server_host, server_port).sync();
        fu.channel().closeFuture().sync();
    }

    static class Handler extends ChannelInboundHandlerAdapter {
        /**
         * 客户端请求的心跳命令
         */
        private static final ByteBuf HEARTBEAT = Unpooled.unreleasableBuffer(// 确保缓冲区不会被意外释放, 可以多次使用，不会因为引用计数为0而被释放
                Unpooled.copiedBuffer("hb_request", CharsetUtil.UTF_8));

        /**
         * 空闲次数 (线程安全：因为仅在 EventLoop 线程中访问)
         */
        private int icount = 1;

        /**
         * 发送次数
         */
        private int scount = 1;

        /**
         * 循环次数
         */
        private int fcount = 1;

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("conn closed");
        }

        /**
         * 处理空闲事件
         * 空闲超过 3 次, 不再发送心跳包
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("userEventTriggered: " + LocalDateTime.now() + ", 次数: " + fcount);
            if (evt instanceof IdleStateEvent e) {
                //如果写通道处于空闲状态,就发送心跳命令
                if (IdleState.WRITER_IDLE.equals(e.state())) {
                    System.out.println("处理 WRITER_IDLE");
                    if (icount <= 3) {
                        icount++;
                        /**
                         * 这里使用 duplicate() 创建一个共享内容但独立位置指针的副本：
                         *  原始缓冲区（由 unreleasableBuffer 保护）保持不变
                         *  副本可以被正常释放，不影响原始缓冲区
                         */
                        ctx.channel().writeAndFlush(HEARTBEAT.duplicate());
                    } else {
                        System.out.println("reach heartbeat sending limitation");
                    }
                    fcount++;
                }
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("第" + scount + "次收到服务端返回: " + msg);
            scount++;
        }
    }
}
