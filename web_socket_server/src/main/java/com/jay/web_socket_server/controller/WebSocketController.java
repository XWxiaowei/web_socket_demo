package com.jay.web_socket_server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiang.wei
 * @date 2020/9/30 7:47 AM
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocketController {
    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;
    /**
     * 标识当前连接客户端的用户名
     */
    private String name;

    private static ConcurrentHashMap<String, WebSocketController> websocketSet = new ConcurrentHashMap<>();

    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name) {
        this.session = session;
        this.name = name;
        //name是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        websocketSet.put(name, this);
        log.info("[WebSocket]连接成功，当前连接人数为={}", websocketSet.size());
    }

    @OnClose
    public void OnClose() {
        websocketSet.remove(this.name);
        log.info("[WebSocket]退出成功，当前连接人数为={}", websocketSet.size());
    }

    @OnMessage
    public void OnMessage(String message) {
        log.info("[WebSocket]收到消息={}", message);
        groupSending("客户端的消息我已经收到了");
    }

    public void groupSending(String message) {
        for (String name : websocketSet.keySet()) {
            try {
                websocketSet.get(name).session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
