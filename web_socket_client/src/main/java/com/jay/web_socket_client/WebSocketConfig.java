//package com.jay.web_socket_client;
//
//import lombok.extern.slf4j.Slf4j;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_6455;
//import org.java_websocket.handshake.ServerHandshake;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
///**
// * @author xiang.wei
// * @date 2020/10/2 3:21 PM
// */
//@Slf4j
//@Component
//public class WebSocketConfig {
//    /**
//     * 客户端的配置
//     *
//     * @return
//     * @author xiagwei
//     * @date 2020/10/2 4:38 PM
//     */
//    @Bean
//    public WebSocketClient webSocketClient() {
//        try {
//            WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:8080/webSocketServer/websocket/name1"), new Draft_6455()) {
//                @Override
//                public void onOpen(ServerHandshake handshakedata) {
//                    log.info("[websocket] 连接成功");
//                }
//
//                @Override
//                public void onMessage(String message) {
//                    log.info("[websocket] 收到消息={}", message);
//
//                }
//
//                @Override
//                public void onClose(int code, String reason, boolean remote) {
//                    log.info("[websocket] 退出连接");
//                }
//
//                @Override
//                public void onError(Exception ex) {
//                    log.info("[websocket] 连接错误={}", ex.getMessage());
//                }
//            };
//            webSocketClient.connect();
//            return webSocketClient;
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
