package org.eu.rainx0.raintool.ex.mqtt.netty.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
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
 * @time 2025/8/7 14:36
 */
public class Client {
    private static final String server_host = "127.0.0.1";
    private static final int server_port = 9527;

    private static final Client instance = new Client();

    private boolean firstStart = true;

    public static void main(String[] args) {
        instance.connect(new Bootstrap(), new NioEventLoopGroup());
    }

    private void connect(Bootstrap boot, EventLoopGroup group) {
        boot.channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new IdleStateHandler(
                                        //读超时时间、写超时时间、所有类型的超时时间、时间单位
                                        //因为服务端设置的超时时间是5秒，所以设置4秒
                                        0, 4, 0, TimeUnit.SECONDS))
                                .addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder())
                                .addLast("handler", new Handler());
                    }
                })
                .remoteAddress(server_host, server_port)
        ;
        ChannelFuture fu = boot.connect()
                /**
                 * 重连监听
                 * 触发时机：连接失败时（如服务器未启动、网络不通等）
                 */
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {

                        if (!channelFuture.isSuccess()) {
                            System.out.println("与服务端断开连接!在10s之后准备尝试重连!");

                            // 获取 channel 原来绑定的 EventLoop, 执行重连
                            EventLoop eventExecutor = channelFuture.channel().eventLoop();
                            eventExecutor.schedule(() ->
                                    connect(new Bootstrap(), eventExecutor), 10, TimeUnit.SECONDS
                            );
                        }
                    }
                });

        if (firstStart) {
            System.out.println("Client started.");
            firstStart = false;
        }

        try {
            fu.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("client start failed");
        }
    }

    static class Handler extends ChannelInboundHandlerAdapter {
        /**
         * 客户端请求的心跳命令
         */
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("hb_request", CharsetUtil.UTF_8));

        /**
         * 空闲次数
         */
        private int idle_count = 1;

        /**
         * 发送次数
         */
        private int count = 1;

        /**
         * 循环次数
         */
        private int fcount = 1;

        /**
         * 建立连接时
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("conn established, " + LocalDateTime.now());
            super.channelActive(ctx);
        }

        /**
         *  channel 关闭时, 触发重连
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("conn closed, " + LocalDateTime.now());
            final EventLoop eventLoop = ctx.channel().eventLoop();
            Client.instance.connect(new Bootstrap(), eventLoop);

            super.channelInactive(ctx);
        }

        /**
         * 心跳请求处理
         * 每4秒发送一次心跳请求;
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
            System.out.println("循环请求的时间：" + LocalDateTime.now() + "，次数" + fcount);
            if (obj instanceof IdleStateEvent event) {
                if (IdleState.WRITER_IDLE.equals(event.state())) {  //如果写通道处于空闲状态,就发送心跳命令
                    if (idle_count <= 2) {   //设置发送次数
                        idle_count++;
                        ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                    } else {
                        System.out.println("不再发送心跳请求了!");
                    }
                    fcount++;
                }
            }
        }

        /**
         * 业务逻辑处理
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("第" + count + "次" + ",客户端接受的消息:" + msg);
            count++;
        }
    }
}
