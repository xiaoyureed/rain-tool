package org.eu.rainx0.raintool.ex.mqtt.netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;

/**
 * @author xiaoyu
 * @time 2025/8/7 14:13
 */
public class HttpServer {

    private static final ServerBootstrap boot = new ServerBootstrap();
    private static final EventLoopGroup boss = new NioEventLoopGroup();
    private static final EventLoopGroup worker = new NioEventLoopGroup();
    private static final int port = 9527;

    public static void main(String[] args) throws InterruptedException {
        boot.channel(NioServerSocketChannel.class)
                .group(boss, worker)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                /**
                                 * // 将原始字节数据解码为 HTTP 对象
                                 * [ByteBuf] --(HttpRequestDecoder)--> [HttpRequest + HttpContent...]
                                 *
                                 *
                                 * HttpServerCodec 同时编解码
                                 */
                                .addLast("decoder", new HttpRequestDecoder())
                                .addLast("encoder", new HttpResponseEncoder())
                                /**
                                 *  消息聚合, 对于入站消息, 将分段的 HTTP 消息聚合为完整的 HTTP 对象
                                 *  最大消息大小限制（10MB）, 防止内存溢出,超过限制会抛出 TooLongFrameException
                                 */
                                .addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024))
                                .addLast("handler", new Handler());
                    }
                });
        ChannelFuture fu = boot.bind(port).sync();
        fu.channel().closeFuture().sync();

    }

    static class Handler extends ChannelInboundHandlerAdapter {

        private void sendResp(ChannelHandlerContext ctx,
                              String content,
                              HttpResponseStatus status) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private String getBody(FullHttpRequest request) {
            ByteBuf buf = request.content();
            return buf.toString(CharsetUtil.UTF_8);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
            ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String result;
            // 因为使用了 HttpObjectAggregator，这里可以直接强转为 FullHttpRequest
            if (!(msg instanceof FullHttpRequest httpRequest)) {
                result = "未知请求!";
                sendResp(ctx, result, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            try {
                String path = httpRequest.uri();            //获取路径
                String body = getBody(httpRequest);    //获取参数
                HttpMethod method = httpRequest.method();//获取请求方法
                //如果不是这个路径，就直接返回错误
                if (!"/test".equalsIgnoreCase(path)) {
                    result = "非法请求!";
                    sendResp(ctx, result, HttpResponseStatus.BAD_REQUEST);
                    return;
                }
                System.out.println("接收到:" + method + " 请求");
                //如果是GET请求
                if (HttpMethod.GET.equals(method)) {
                    //接受到的消息，做业务逻辑处理...
                    System.out.println("body:" + body);
                    result = "GET请求";
                    sendResp(ctx, result, HttpResponseStatus.OK);
                    return;
                }
                //如果是POST请求
                if (HttpMethod.POST.equals(method)) {
                    //接受到的消息，做业务逻辑处理...
                    System.out.println("body:" + body);
                    result = "POST请求";
                    sendResp(ctx, result, HttpResponseStatus.OK);
                    return;
                }

                //如果是PUT请求
                if (HttpMethod.PUT.equals(method)) {
                    //接受到的消息，做业务逻辑处理...
                    System.out.println("body:" + body);
                    result = "PUT请求";
                    sendResp(ctx, result, HttpResponseStatus.OK);
                    return;
                }
                //如果是DELETE请求
                if (HttpMethod.DELETE.equals(method)) {
                    //接受到的消息，做业务逻辑处理...
                    System.out.println("body:" + body);
                    result = "DELETE请求";
                    sendResp(ctx, result, HttpResponseStatus.OK);
                    return;
                }
            } catch (Exception e) {
                System.out.println("处理请求失败!");
                e.printStackTrace();
            } finally {
                //释放请求
                httpRequest.release();
            }
        }
    }
}
