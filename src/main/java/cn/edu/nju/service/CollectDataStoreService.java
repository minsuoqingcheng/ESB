package cn.edu.nju.service;

import cn.edu.nju.entity.CollectData;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CollectDataStoreService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void store(JSONObject data) {
        CollectData collectData = JSONObject.toJavaObject(data, CollectData.class);
        mongoTemplate.insert(collectData);
    }

}
