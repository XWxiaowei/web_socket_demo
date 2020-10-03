package com.jay.web_socket_client.controller;

import com.alibaba.fastjson.JSON;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiang.wei
 * @date 2020/10/2 10:04 PM
 */
public class OkHttpClient2Service {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        String url = "ws://localhost:8080/webSocketServer/websocket/name2";
        Map<String, String> map = new HashMap<>(1);
        map.put("message","name3需要发送的消息");
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(map)))
                .url(url).build();
        ResponseBody body = client.newCall(request).execute().body();
        System.out.println("*******返回结果={}" + body.string());
    }
}
