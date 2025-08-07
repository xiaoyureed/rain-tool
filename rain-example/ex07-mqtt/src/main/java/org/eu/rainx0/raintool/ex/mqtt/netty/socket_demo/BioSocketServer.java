package org.eu.rainx0.raintool.ex.mqtt.netty.socket_demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xiaoyu
 * @time 2025/8/3 13:35
 */
public class BioSocketServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9090);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> {
                try {
                    handleClient(clientSocket);
                } catch (Exception e) {
                    System.out.println("[" + clientSocket.getInetAddress().toString() + "] error: " + e.getMessage());
                }
            }).start();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {

        byte[] buf = new byte[1024];
        InputStream is = clientSocket.getInputStream();

//        ...
    }
}
