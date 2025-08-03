package org.eu.rainx0.raintool.ex.netty.socket_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 非阻塞
 * 一个 thread 不断转, 可以处理非常多的请求
 *
 * weakness:
 *  - 即使没有请求来, thread 也会空转, 浪费 cpu
 *  - client 列表, 每次都会遍历, 可能存在数据收发的 client 只有几个, 但是也会全量遍历, 造成浪费
 *
 * @author xiaoyu
 * @time 2025/8/3 13:51
 */
public class NioSocketServer {

    static List<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(9090));
        serverChannel.configureBlocking(false);

        while (true) {
            // accept() shall not block,
            // If there is no connection request, it will return null
            //nio 是OS 内部实现的, 底层是 Linux 的 accept 方法支持的
            SocketChannel clientChannel = serverChannel.accept();
            if (clientChannel != null) {
                clientChannel.configureBlocking(false);
                clients.add(clientChannel);
            }

            Iterator<SocketChannel> it = clients.iterator();
            while (it.hasNext()) {
                SocketChannel client = it.next();
                ByteBuffer buf = ByteBuffer.allocate(6);

                // non-blocking here
                int len = client.read(buf);

                if (len > 0) {
                    System.out.println("Receive: " + new String(buf.array()));
                }
                // client 断开连接
                else if (len == -1){
                    it.remove();
                    client.close();
                }
            }
        }

    }
}
