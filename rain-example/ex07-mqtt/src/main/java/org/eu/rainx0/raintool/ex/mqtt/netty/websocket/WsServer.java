package org.eu.rainx0.raintool.ex.mqtt.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author xiaoyu
 * @time 2025/8/8 16:14
 */
public class WsServer {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new ServerBootstrap().group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<ServerSocketChannel>() {
                    @Override
                    protected void initChannel(ServerSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(
                                        0,
                                        0,
                                        1 * 60 * 60
                                ) {
                                    // 如果在1小时没有向服务端发送读写心跳(ALL)，则服务端主动断开连接
                                    @Override
                                    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
                                        if (IdleState.ALL_IDLE.equals(evt.state())) {
                                            ctx.close();
                                        }
                                        super.channelIdle(ctx, evt);
                                    }
                                })

                                //websocket 基于http协议，所以需要有http的编解码器
                                .addLast(new HttpServerCodec())
                                //添加对大数据流的支持
                                .addLast(new ChunkedWriteHandler())
                                //对httpMessage进行聚合，聚合成为FullHttpRequest或FullHttpResponse
                                //几乎在netty的编程中，都会使用到此handler
                                // 单位: 字节
                                .addLast(new HttpObjectAggregator(1024 * 1024)) // 1MB
                                .addLast(new WebSocketServerProtocolHandler("/ws"))
                                .addLast(new ChatHandler())
                        ;
                    }

                })
                .bind(9527).sync();

        System.out.println("Server started.");

        fu.channel().closeFuture().sync();
    }

    static class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
        // 存储所有连接的Channel
        private ChannelGroup clients;

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

            //clients = new DefaultChannelGroup(ctx.executor());
            clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            super.handlerAdded(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            clients.close();

            super.handlerRemoved(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            clients.add(ctx.channel());
            System.out.println("客户端连接: " + ctx.channel().id());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            clients.remove(ctx.channel());
            System.out.println("客户端断开: " + ctx.channel().id());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame frame) throws Exception {
            String msg = frame.text();
            System.out.println("收到消息: " + msg);

            // 方案1：广播给所有客户端
            clients.writeAndFlush(new TextWebSocketFrame("[广播] " + msg));

            // 方案2：转发给特定客户端（示例逻辑）
            if (msg.startsWith("@")) {
                forwardToTargetClient(msg);
            }
        }

        private void forwardToTargetClient(String msg) {
            String targetId = msg.substring(1, msg.indexOf(" "));
            String content = msg.substring(msg.indexOf(" ") + 1);

            clients.stream()
                    .filter(ch -> ch.id().asShortText().equals(targetId))
                    .findFirst()
                    .ifPresent(ch ->
                            ch.writeAndFlush(new TextWebSocketFrame("[私信] " + content))
                    );
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            clients.remove(ctx.channel());
            ctx.close();
            cause.printStackTrace();
        }
    }
}
