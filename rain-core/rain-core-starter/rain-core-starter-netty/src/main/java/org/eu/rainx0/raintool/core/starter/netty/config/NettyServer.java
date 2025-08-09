package org.eu.rainx0.raintool.core.starter.netty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoyu
 * @time 2025/8/7 21:23
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "rain.netty.server", name = "port", matchIfMissing = false)
public class NettyServer implements CommandLineRunner {

    private final NettyProps nettyProps;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;


    public void shutdown() {
        try {
            if (boss != null) {
                boss.shutdownGracefully().sync();
            }
            if (worker != null) {
                worker.shutdownGracefully().sync();
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void start() {
        Integer port = nettyProps.getServer().getPort();

        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();

        ServerBootstrap boot = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        int maxReadIdleSeconds = nettyProps.getServer().getMaxReadIdleSeconds();

                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                //.addLast(new IdleStateHandler(maxReadIdleSeconds,
                                //        0, 0, TimeUnit.SECONDS) {
                                //    @Override
                                //    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
                                //        if (IdleState.READER_IDLE.equals(evt.state())) {
                                //            log.debug(";; {} 内没有读取到消息, 关闭连接", maxReadIdleSeconds);
                                //            ctx.close();
                                //        }
                                //
                                //    }
                                //})

                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 1024)) //单位:字节, 1024 kb = 1MB
                                .addLast(new WebSocketServerProtocolHandler(nettyProps.getServer().getContextPath()))
                                .addLast(new ServerHandler())
                        ;
                    }
                })
                //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //将小的数据包包装成更大的帧进行传送，提高网络的负载
                .childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture fu;
        try {
            fu = boot.bind(port).sync();

            log.info(";;netty server started at :{}", port);

            fu.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.shutdown();
        }
    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        this.start();
    }

    static class ServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

        private static final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            channels.putIfAbsent(ctx.channel().id().asLongText(), ctx.channel());

            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            channels.remove(ctx.channel().id().asLongText());
            super.channelInactive(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext c, WebSocketFrame frame) throws Exception {
            if (frame instanceof TextWebSocketFrame textFrame) {
                String msg = textFrame.text();
                System.out.println("收到消息: " + msg);
            }
        }
    }
}
