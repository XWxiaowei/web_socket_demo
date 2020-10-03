//package com.jay.web_socket_client.controller;
//import org.java_websocket.client.WebSocketClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author xiang.wei
// * @date 2020/10/2 5:40 PM
// */
//@RestController
//@RequestMapping("/websocket")
//public class WebSocketClientController {
//    @Autowired
//    private WebSocketClient webSocketClient;
//
//    @RequestMapping("/index")
//    public String sendMessage(String message) {
//        webSocketClient.send("测试消息");
//        return "消息发送成功";
//    }
//}
