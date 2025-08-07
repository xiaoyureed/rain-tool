package org.eu.rainx0.raintool.ex.mqtt.netty.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.*;
import io.netty.util.ReferenceCountUtil;

import java.time.LocalDateTime;

/**
 * @author xiaoyu
 * @time 2025/8/7 17:52
 */
public class ProtobufClient {
    private static final Bootstrap boot = new Bootstrap();
    private static final String server_host = "127.0.0.1";
    private static final int server_port = 9527;
    private static final NioEventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = boot.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                /**
                                 * 用于解码基于长度的 Protobuf 消息帧为字节, 实现了 Google Protocol Buffers 的 Length-delimited 格式
                                 * - 读取 varint32 格式的长度字段
                                 * - 根据长度字段提取完整的消息体
                                 * - 解决 TCP 粘包和拆包问题
                                 *
                                 * 解码的数据格式: [length][protobuf data]
                                 */
                                .addLast(new ProtobufVarint32FrameDecoder())
                                /**
                                 * 将字节数据解码为具体的 Protobuf Message 对象
                                 */
                                .addLast(new ProtobufDecoder(UserProto.UserMsg.getDefaultInstance()))
                                // 用于出站编码, 和ProtobufVarint32FrameDecoder 配对使用
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                // 出站编码, 和 protobuf decoder 配对
                                .addLast(new ProtobufEncoder())
                                .addLast(new Handler())
                        ;

                    }
                })
                .connect(server_host, server_port)
                .sync();
        System.out.println("client started.");

        fu.channel().closeFuture().sync();
    }

    static class Handler extends ChannelInboundHandlerAdapter {

        /** 循环次数 */
        private int fcount = 1;

        /**
         * 建立连接时
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("建立连接时：" + LocalDateTime.now());
            ctx.fireChannelActive();
        }

        /**
         * 关闭连接时
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("关闭连接时：" + LocalDateTime.now());
            super.channelInactive(ctx);
        }

        /**
         * 业务逻辑处理
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                // 如果不是protobuf类型的数据
                if (!(msg instanceof UserProto.UserMsg userMsg)) {
                    System.out.println("未知数据!" + msg);
                    return;
                }

                System.out.println("客户端接受到的用户信息。编号:" + userMsg.getId() + ",姓名:" + userMsg.getName() + ",年龄:" + userMsg.getAge());

                // 这里返回一个已经接受到数据的状态
                UserProto.UserMsg.Builder userState = UserProto.UserMsg.newBuilder().setState(1);
                ctx.writeAndFlush(userState);
                System.out.println("成功发送给服务端!");
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * 不需要释放的消息类型：
             * String
             * Integer
             * Long
             * 自定义 POJO（未实现 ReferenceCounted）
             *
             * 需要释放的消息类型：
             * ByteBuf
             * Protobuf Message（某些实现）
             * 实现 ReferenceCounted 接口的对象
             */
            finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
