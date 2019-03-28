package cn.edu.nju.service;

import cn.edu.nju.constant.DataType;
import cn.edu.nju.entity.CollectData;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectDataStoreService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void store(JSONObject data) {
        CollectData collectData = JSONObject.toJavaObject(data, CollectData.class);
        mongoTemplate.insert(collectData);
    }

    public List<CollectData> getAllByType(DataType type) {
        return mongoTemplate.find(Query.query(Criteria.where("type").is(type.getType())), CollectData.class);
    }
}
