package com.jay.web_socket_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author xiang.wei
 * @date 2020/10/2 3:21 PM
 */
@Slf4j
@Component
public class WebSocketConfig {
    /**
     * ServerEndpointExporter作用
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
