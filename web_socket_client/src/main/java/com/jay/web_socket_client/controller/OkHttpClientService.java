package com.jay.web_socket_client.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.concurrent.TimeUnit;

/**
 * @author xiang.wei
 * @date 2020/10/2 6:55 PM
 */
public class OkHttpClientService extends WebSocketListener {
    private String result = null;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        webSocket.send("需要发送的请求数据");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        //其中text是接收到的参数
        result = text;
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                //设置超时时间是5秒
                .connectTimeout(5, TimeUnit.MINUTES)
                .build();
        //http的请求对应的就是websocket中的ws;https的请求对应的就是websocket中的wss
        String url = "ws://localhost:8080/webSocketServer/websocket/name2";
        //实例化Request对象
        Request request = new Request.Builder().url(url).build();
        OkHttpClientService okHttpClientService = new OkHttpClientService();
        client.newWebSocket(request, okHttpClientService);
        //轮询获取结果
        while (okHttpClientService.result == null) {

        }
        System.out.println("接口响应的结果=" + okHttpClientService.result);
    }
}
