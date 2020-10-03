# web_socket_demo
WebSocket的demo
## 博客地址
https://feige.blog.csdn.net/article/details/108906050
## 前言
这两天在调试一个WebSocket的接口，折腾了一天的时间终于弄好了。现在对WebSocket的相关知识点做一个记录。主要从如下几个方面进行介绍。
### WebSocket的概念
HTTP请求是基于请求响应的模式，永远是客户端请求服务器端，是单向的请求。如果服务器端有连续的状态变化，客户端就需要通过轮询的方式去获知。也就是每隔一段时间，就发出一个询问，了解服务器有没有新消息，轮询的效率比较低，非常浪费资源。
WebSocket的最大特点就是服务器可以主动向客户端推送消息，客户端也可以主动向服务器端发送消息，是真正的双向平等通信，也就是全双工通信。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201002232005601.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ1MzQ4MDg=,size_16,color_FFFFFF,t_70#pic_center)
### WebSocket的特点
1. 建立在TCP协议之上，服务器的实现比较容易。
2. 与HTTP协议有着良好的兼容性，默认端口号也是80和443，并且握手阶段采用的是HTTP协议，因此握手时不容易屏蔽，能通过各种HTTP代理服务器。
3. 数据格式比较轻量，性能开销小，通信高效。
4. 没有同源限制，原生支持跨域，客户端可以与任意服务器通信。
5. 协议标识符是ws（如果加密，则为wss），服务器网址就是URL。
#### WebSocket的工作过程
建立一个WebSocket连接，客户端浏览器首先要向服务器发起一个HTTP请求，这个请求和通常的HTTP请求不同，包含了一些附加头信息。
客户端请求
```
GET / HTTP/1.1
Request URL: ws://localhost:8080/webSocketServer/websocket/testname
Connection: Upgrade
Host: localhost:8080
Origin: http://localhost:63342
Pragma: no-cache
Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits
Sec-WebSocket-Key: oxTUjq93ipRDk4gXWhi+mg==
Sec-WebSocket-Version: 13
Upgrade: websocket
```
服务器响应
```
Connection: upgrade
Date: Sat, 03 Oct 2020 03:26:03 GMT
Sec-WebSocket-Accept: Jjxh2cOWicbJdcdZ3rhcAeNdoHQ=
Sec-WebSocket-Extensions: permessage-deflate;client_max_window_bits=15
Upgrade: websocket
```
Connection 必须设置Upgrade,表示客户端希望连接升级。
Upgrade字段必须设置WebSocket，表示希望升级到Websocket协议。
Sec-WebSocket-Key 是随机的字符串，服务器会用这些数据来构造出一个SHA-1的信息摘要，把"
Sec-WebScoket-Key"加上一个特殊字符串"oxTUjq93ipRDk4gXWhi+mg=="然后计算 SHA-1 摘要，之后进行 BASE-64编码，将结果做为 “Sec-WebSocket-Accept” 头的值，返回给客户端。如此操作，可以尽量避免普通 HTTP 请求被误认为Websocket 协议。
服务器解析这些附加的头信息然后产生应答信息返回给客户端，客户端和服务器端的WebSocket连接就建立起来了，双方就可以通过这个连接通道自由的传递信息，并且这个连接会持续存在直到客户端或者服务器端的某一方主动的关闭连接，http和WebSocket的连接生命周期如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201003113008717.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ1MzQ4MDg=,size_16,color_FFFFFF,t_70#pic_center)
## 在SpringBoot中整合WebSocket 
### 服务端
#### 引入依赖
首先我们需要引入WebSocket的starter模块，这个模块的作用开启WebSocket的模块功能，如下所示：
```xml
   <!--引入websocket-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
```
#### 配置基础类

依赖引入之后，我们需要配置ServerEndpointExporter的Bean,这个Bean作用是我们可以通过`@ServerEndpoint`注解声明WebSocket。

```java
@Component
public class WebSocketConfig {
    /**
     * ServerEndpointExporter作用
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```
#### 编写websocket的服务端接口
准备工作做好之后，接下来就是编写WebSocket的服务端的接口，在此Controller中，定义了Session类，通过它来给客户端发送消息，通过ConcurrentHashMap来存储当前连接的客户端。
```java
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
```
我们通过`@ServerEndpoint`注解标识这个类是一个WebSocket接口，通过`@OnOpen`标记的方法进行WebSocket的服务连接，通过`@OnMessage`标记的方法进行消息的接收，以及响应消息给客户端，其中给客户端发送消息
`websocketSet.get(name).session.getBasicRemote().sendText(message);` getBasicRemote()的方法表示通过同步的方式发送消息，getAsyncRemote()的方法表示通过异步的方式发送消息。通过`@OnClose`标记的方法进行服务的关闭。
### 客户端的调用
客户端的调用有好几种方式，我们可以通过JS的方式，实例化WebSocketClient的方式，运用OkHttpClient的方式，下面就分别对这三种方式的调用做一个介绍。

### 通过JS的方式调用服务

```
        var websocket = null;
        if ('WebSocket' in window) {
            //用于创建WebSocket对象，webSocketTest对应的是java类的注解值。
            websocket = new WebSocket("ws://localhost:8080/webSocketServer/websocket/testname");
        } else {
            alert("当前浏览器不支持");
        }
//        连接发生错误的时候回调方法；
        websocket.onerror = function () {
            alert("连接错误");
        }
//       连接成功时建立回调方法
        websocket.onopen = function () {
            //WebSocket已连接上，使用send()方法发送数据
            alert("连接成功");
        };
//      收到消息的回调方法
        websocket.onmessage = function (msg) {
            setdivInnerHTML(msg.data);
        };
        //连接关闭的回调方法
        websocket.onclose = function () {
            closed();
            alert("关闭成功");
        };

        function closed() {
            websocket.close();
            alert("点击关闭");
        }
		
        function send() {
            var message=document.getElementById("message").value;   //注意引号内的内容应该是文本框的id而不能是name
            alert(message);
            websocket.send(message); //给后台发送数据
        }
```
同样的首先是实例化一个WebSocket对象，然后通过onmessage回调方法接受服务端响应的结果，通过send方法给后台发送数据。
### 通过实例化WebSocketClient的方式
1. 引入依赖
```xml
<dependency>
			<groupId>org.java-websocket</groupId>
			<artifactId>Java-WebSocket</artifactId>
			<version>1.3.5</version>
		</dependency>
```
同样的我们需要进行客户端的配置，也就是实例化一个WebSocketClient的实例。主要是建立连接，发送消息。

```java
   @Bean
    public WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:8080/webSocketServer/websocket/name1"), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("[websocket] 连接成功");
                }
                @Override
                public void onMessage(String message) {
                    log.info("[websocket] 收到消息={}", message);
                }
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] 退出连接");
                }
               @Override
                public void onError(Exception ex) {
                    log.info("[websocket] 连接错误={}", ex.getMessage());
                }
            };
            webSocketClient.connect();
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
```
然后在Controller中通过WebSocketClient的send方法给服务器端发送消息，如下所示;

```java
@RestController
@RequestMapping("/websocket")
public class WebSocketClientController {
    @Autowired
    private WebSocketClient webSocketClient;

    @RequestMapping("/index")
    public String sendMessage(String message) {
        webSocketClient.send("测试消息");
        return "消息发送成功";
    }
}

```
这是一种通过后端接口调用服务的方式，当然我们还有其他的方式，就像下面这种通过OkHttpClient的方式。
### 通过OkHttpClient的方式
同样的还是先引入依赖。
```xml
		<dependency>
			<groupId>com.squareup.okhttp3</groupId>
			<artifactId>okhttp</artifactId>
		    <version>3.9.1</version>
		</dependency>
```
然后定义一个类，继承WebSocketListener，用于建立连接，发送消息。

```java
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
```
同样的，还是重写了onOpen方法和onMessage方法，onOpen方法用来发送消息，onMessage方法用来接收服务器的请求。如果是异步的消息的话，我们就需要轮询获取结果。
## 总结
本文简单首先介绍了WebSocket的基本概念和相关特点，WebSocket是一个全双工通信的协议，它支持客户端向服务端发送消息， 也支持服务端想客户端发送消息， 一次握手，可以多次发送消息。接着就是介绍了在SpringBoot中如何整合WebSocket的相关功能。实现了一个服务端和客户端。
## 源码地址
https://github.com/XWxiaowei/web_socket_demo.git
## 参考
https://www.jianshu.com/p/9aa969dd1b4d
https://blog.csdn.net/weixin_38111957/article/details/86352677
