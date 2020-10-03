package java.com.jay.web_socket_server;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author xiang.wei
 * @ServerEndpoint 注解是一个类层次的注解，
 * 它的功能主要是是将目前的类定义成一个webSocket服务器端，注解的值将被用于监听用户连接的终端
 * 访问URL地址，客户端可以通过这个URL来连接WebSocket服务器端。
 * @date 2020/9/30 8:45 PM
 */
@ServerEndpoint("/WebSocketTest")
public class WebSocketServer {

    private Session session;

    @OnOpen //打开连接执行
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("****打开了连接");
    }

    @OnMessage //收到消息执行
    public void onMessage(String message, Session session) {
        System.out.println(message);
        sendMessage(message);
    }

    @OnClose //关闭连接执行
    public void onClose(Session session) {
        System.out.println("关闭连接");
    }

    /**
     * webSocket session发送文本消息有两个方法：getAsyncRemote()和
     * getBasicRemote() getAsyncRemote()和getBasicRemote()是异步与同步的区别，
     * 大部分情况下，推荐使用getAsyncRemote()。
     * @param message
     */
    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
}
