package org.eu.rainx0.raintool.ex.mqtt.netty.pojocodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyu
 * @time 2025/8/8 13:34
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture fu = new ServerBootstrap().group(new NioEventLoopGroup(1), new NioEventLoopGroup(10))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<ServerChannel>() {
                    @Override
                    protected void initChannel(ServerChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new PojoDecoder())
                                .addLast("encoder", new PojoEncoder())
                                .addLast("hadnler", new Handler())
                                ;
                    }
                })
                .bind(9527).sync();

        System.out.println("Server started.");

        fu.channel().closeFuture().sync();
    }

    /**
     * 出站编码器
     */
    static class PojoEncoder extends MessageToByteEncoder<Serializable> {

        // 存放消息长度
        private static final byte[] length_placeholder = new byte[4];


        @Override
        protected void encode(ChannelHandlerContext c, Serializable msg, ByteBuf out) throws Exception {
            int lenIndex = out.writerIndex();

            //out.writeInt(0); // 预留 4 字节
            out.writeBytes(length_placeholder);

            int contentIndex = out.writerIndex();

            //new ByteArrayOutputStream()
            ByteBufOutputStream bout = new ByteBufOutputStream(out);
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(bout);
                oos.writeObject(msg);
                oos.flush();
            } finally {
                if (oos != null) {
                    oos.close();
                } else {
                    bout.close();
                }
            }

            int contentLen = out.writerIndex() - contentIndex;
            out.setInt(lenIndex, contentLen);

        }
    }

    /**
     * 进站解码器
     */
    static class PojoDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext c, ByteBuf in, List<Object> out) throws Exception {
            // 检查是否有足够的数据读取长度字段
            if (in.readableBytes() < 4) {
                return;
            }
            // 读取长度字段
            int length = in.readInt();
            // 标记当前读取位置
            in.markReaderIndex();

            // 检查是否有足够的数据读取消息体
            if (in.readableBytes() < length) {
                // 恢复读取位置，等待更多数据
                in.resetReaderIndex();
                return;
            }

            // 读取消息体
            ByteBuf content = in.readBytes(length);

            // 反序列化对象
            ByteBufInputStream bin = new ByteBufInputStream(content);
            ObjectInputStream oin = new ObjectInputStream(bin);
            Object obj = oin.readObject();

            // 添加到输出列表
            out.add(obj);

            // 关闭流
            oin.close();
            content.release();
        }
    }

    static class Handler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }
    }
}
