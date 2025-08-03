package org.eu.rainx0.raintool.ex.netty.socket_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author xiaoyu
 * @time 2025/8/3 14:20
 */
public class NioSelectorSocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(9090));
        serverChannel.configureBlocking(false);

        // 创建 selector, 处理 channel, 即创建 epoll
        Selector selector = Selector.open();
        // 将 server channel 注册到 selector 上, 指定监听 accept 事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 阻塞, 等待关注的事件发生
            // 同时让出 cpu
            selector.select();

            for (SelectionKey sk : selector.selectedKeys()) {
                // 处理 accept 事件
                if (sk.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) sk.channel();
                    // server channel 已经设置为非阻塞了, 这里不会阻塞
                    SocketChannel clientChannel = server.accept();

                    clientChannel.configureBlocking(false);
                    // 注册进selector, 关注读事件
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }
                // 处理 read 事件
                else if (sk.isReadable()) {
                    SocketChannel client = (SocketChannel) sk.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = client.read(buf);
                    if (len > 0) {
                        System.out.println("Receive: " + new String(buf.array()));
                    } else {
                        client.close();
                    }
                }
            }

        }

    }
}
