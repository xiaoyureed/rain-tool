package org.eu.rainx0.raintool.ex.websocket.tomcat;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
 * @time 2025/8/8 17:51
 */
@Component
@ServerEndpoint("/ws/tomcat") // 基于 Tomcat 原生支持的注解实现
public class WsService {

    @OnOpen
    public void onOpen() {
        System.out.println("tomcat onOpen");
    }

    @OnClose
    public void onClose() {
        System.out.println("tomcat onClose");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("tomcat onMessage: " + message);
    }

    @OnError
    public void onError(Throwable throwable) {
        System.out.println("tomcat onError: " + throwable);
    }
}
