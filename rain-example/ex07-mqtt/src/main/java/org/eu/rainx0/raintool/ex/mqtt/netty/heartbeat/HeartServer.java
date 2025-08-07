package org.eu.rainx0.raintool.ex.mqtt.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 * @time 2025/8/7 12:50
 */
public class HeartServer {
    private static final ServerBootstrap boot = new ServerBootstrap();
    private static final EventLoopGroup boss = new NioEventLoopGroup();
    private static final EventLoopGroup worker = new NioEventLoopGroup();
    private static final int port = 9527;

    public static void main(String[] args) throws InterruptedException {
        try {

            boot.channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    //读超时时间、写超时时间、所有类型的超时时间、时间格式
                                    //5s 没收到心跳则触发事件
                                    // 也可通过继承IdleStateHandler , 覆盖方法，实现自定义处理
                                    .addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
                                    .addLast("decoder", new StringDecoder())
                                    .addLast("encoder", new StringEncoder())
                                    .addLast("handler", new Handler());

                        }
                    });
            ChannelFuture fu = boot.bind(port).sync();
            System.out.println("server started");

            fu.channel().closeFuture().sync();
        } finally {

            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    static class Handler extends ChannelInboundHandlerAdapter {
        /**
         * 空闲次数
         */
        private int idle_count = 1;
        /**
         * 发送次数
         */
        private int count = 1;

        /**
         * 超时处理
         * 如果超过两次，则直接关闭连接;
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
            if (obj instanceof IdleStateEvent event) {
                if (IdleState.READER_IDLE.equals(event.state())) {  //如果读通道处于空闲状态，说明没有接收到心跳命令
                    System.out.println("已经5秒没有接收到客户端的信息了");
                    if (idle_count > 2) {
                        System.out.println("关闭这个不活跃的channel");
                        //ctx.channel().close();
                        // 等价
                        ctx.close();
                    }
                    idle_count++;
                }
            } else {
                super.userEventTriggered(ctx, obj);// 不关注的事件传递给下一个handler
            }
        }


        /**
         * 业务逻辑处理
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("第" + count + "次读到消息:" + msg);
            String message = (String) msg;
            if ("hb_request".equals(message)) {  //如果是心跳命令，则发送给客户端;否则什么都不做
                ctx.write("服务端成功收到心跳信息");
                ctx.flush();
            }
            count++;
        }

        /**
         * 异常处理
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
