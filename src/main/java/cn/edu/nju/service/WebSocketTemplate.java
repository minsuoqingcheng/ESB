package cn.edu.nju.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketTemplate {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    //向订阅了 /topic/real-time 客户端webSocket实例发送数据
    public void sendMessage(JSONObject data) {
        messagingTemplate.convertAndSend("/topic/real-time", data);
    }
}
