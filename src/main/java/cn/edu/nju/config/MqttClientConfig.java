package cn.edu.nju.config;

import cn.edu.nju.service.ApplicationWebSocket;
import cn.edu.nju.service.CollectDataStoreService;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Configuration
public class MqttClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttClientConfig.class);

    @Value("${spring.mqtt.broker}")
    private String broker;
    @Value("${spring.mqtt.clientId}")
    private String clientId;
    @Value("${spring.mqtt.username}")
    private String username;
    @Value("${spring.mqtt.password}")
    private String password;
    @Value("${spring.mqtt.topic}")
    private String topic;
    @Value("${spring.mqtt.qos}")
    private int qos;

    @Autowired
    private CollectDataStoreService storeService;


    private static final Executor executor = Executors.newFixedThreadPool(4);


    @Bean
    public MqttClient mqttClient() {
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            // 创建链接参数
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // 在重新启动和重新连接时记住状态
            connOpts.setCleanSession(true);
            // 设置连接的用户名
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            connOpts.setKeepAliveInterval(20);
            // 设置回调函数
            mqttClient.setCallback(new MqttCallback() {

                public void connectionLost(Throwable cause) {
                    logger.info("connection lost");
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String data = new String(message.getPayload());
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    executor.execute(() -> {
                        ApplicationWebSocket.sendInfo(jsonObject.toJSONString());
                        storeService.store(jsonObject);
                    });
                }

                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            // 建立连接
            mqttClient.connect(connOpts);
            //订阅消息
            mqttClient.subscribe(topic, qos);
            return mqttClient;
        } catch (MqttException e) { 
            logger.error("create mqttClient failed, exception is " + e.getMessage());
            throw new RuntimeException("create mqttClient failed, exception is " + e.getMessage());
        }
    }



}
