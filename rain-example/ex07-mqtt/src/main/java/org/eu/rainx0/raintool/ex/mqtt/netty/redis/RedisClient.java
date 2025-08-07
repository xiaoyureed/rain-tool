package org.eu.rainx0.raintool.ex.mqtt.netty.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author xiaoyu
 * @time 2025/8/7 20:17
 */
public class RedisClient {
    public static void main(String[] args) {

    }

    static class Handler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = ctx.alloc().buffer();

            /**
             *  set name sidiot
             *
             * 每条指令之后都要添加回车与换行符 \r\n
             *
             * ```
             * *3\r\n       # 整个指令一共有 3 部分，因此用 *3 表示
             * $3\r\n set\r\n     # set指令的长度是 3
             * $4\r\n name\r\n    # name 长度是 4
             * $6\r\n sidiot\r\n  # sidiot 长度是 6
             * ```
             */
            String command = """
                    *3\r\n
                    $3\r\n SET\r\n
                    $4\r\n name\r\n
                    $6\r\n sidiot\r\n
                    """;
            buf.writeBytes(command.getBytes());
            ctx.writeAndFlush(buf);

            super.channelActive(ctx);
        }
    }
}
