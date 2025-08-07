package org.eu.rainx0.raintool.core.starter.netty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 * @time 2025/8/7 21:23
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "rain.netty.server", name = "port", matchIfMissing = false)
public class NettyServer implements InitializingBean, DisposableBean {

    private final NettyProps nettyProps;

    private NettyProps.Server serverProps() {
        return nettyProps.getServer();
    }

    private final NioEventLoopGroup boss = new NioEventLoopGroup();
    private final NioEventLoopGroup worker = new NioEventLoopGroup();


    @Override
    public void destroy() throws Exception {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
        log.debug(";;Netty server shutdown");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBootstrap boot = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(serverProps().getMaxReadIdleSeconds(),
                                        0, 0, TimeUnit.SECONDS) {

                                    @Override
                                    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
                                        log.debug(";; {} 内没有读取到消息, 关闭连接", serverProps().getMaxReadIdleSeconds());
                                        ctx.close();
                                    }
                                })
//                                .addLast()
                        ;
                    }
                })
                //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //将小的数据包包装成更大的帧进行传送，提高网络的负载
                .childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture fu = boot.bind(serverProps().getPort()).sync();

        log.debug(";;netty server started.");

        fu.channel().closeFuture().sync();

    }
}
