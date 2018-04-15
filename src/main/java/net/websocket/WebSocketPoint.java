package net.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * WebSocket服务器,在缺省情况下，wss的端口是443
 *
 * @author Guan
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketPoint {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketPoint.class);
    private static final CopyOnWriteArraySet<WebSocketPoint> ALL_CLIENT_SET = new CopyOnWriteArraySet<>();


    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        ALL_CLIENT_SET.add(this);
        logger.info("WebSocket有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        ALL_CLIENT_SET.remove(this);
        logger.info("WebSocket有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("来自客户端的消息:" + message);
        String response_str = "";
        this.sendMessage(response_str);
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        ALL_CLIENT_SET.remove(this);
        logger.info("发生错误,关闭连接！当前在线人数为" + getOnlineCount());
//        error.printStackTrace();
    }

    /**
     * 通过WebSocket向Web端发送消息
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前所有Web在线Client的数量
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return ALL_CLIENT_SET.size();
    }


    /**
     * 获取当前所有Web在线Client的数量
     *
     * @return
     */
    public static synchronized String getWSInfo() {
        StringBuilder sb = new StringBuilder();
        for (WebSocketPoint point : ALL_CLIENT_SET) {
            sb.append(point.toString()).append("\n");
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return "WSServer{" +
                "session=" + session.toString() +
                "session.getBasicRemote()=" + session.getBasicRemote().toString() +
                '}';
    }

    /**
     * 通知每一个WebSocket Client,更新信息
     *
     * @param msg
     */
    public static void updateAllClient(String msg) {
        for (WebSocketPoint point : ALL_CLIENT_SET) {
            point.sendMessage(msg);
        }
    }


}
