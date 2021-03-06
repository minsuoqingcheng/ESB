package cn.edu.nju.controller;

import cn.edu.nju.constant.DataType;
import cn.edu.nju.entity.CollectData;
import cn.edu.nju.service.CollectDataStoreService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@RestController("/")
public class NonStreamDataController {

    private static final Executor executor = Executors.newFixedThreadPool(4);

    @Autowired
    private CollectDataStoreService storeService;


    @GetMapping("/non_stream/all")
    public List<CollectData> getAll() {
        return storeService.getAllByType(DataType.NON_STREAM);
    }

    @PutMapping("/non_stream")
    public void nonStream(@RequestBody JSONObject data) {
        executor.execute(() -> storeService.store(data));
    }
}
