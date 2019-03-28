package cn.edu.nju.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(value = "/")
@Component
public class ApplicationWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationWebSocket.class);


    //concurrent包的线程安全Set，用来存放每个客户端对应的ApplicationWebSocket对象。
    private static CopyOnWriteArraySet<ApplicationWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("getMessage, message is:" + message);
    }

    /**
     * 发生错误时调用
     @OnError
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("error", error.getMessage());
    }



    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("error is " + e.getMessage());
        }
    }


     /**
      * 群发自定义消息
      * */
    public static void sendInfo(String message) {
        for (ApplicationWebSocket item : webSocketSet) {
            item.sendMessage(message);
        }
    }

}
