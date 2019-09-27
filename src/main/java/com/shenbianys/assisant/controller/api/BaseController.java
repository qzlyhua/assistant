package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.service.MongoService;
import com.shenbianys.assisant.service.impl.MysqlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Yang Hua
 */
@Controller
public class BaseController {
    @Autowired
    private MysqlServiceImpl mysqlService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    protected AsyncTask asyncTask;

    protected List<Map<String, Object>> queryForList(String env, String sql) {
        return mysqlService.queryForList(env, sql);
    }

    /**
     * 异步查询各环境数据
     *
     * @param sql
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    protected Map<String, List<Map<String, Object>>> queryForListFromAll(String sql) throws ExecutionException, InterruptedException {
        Map<String, List<Map<String, Object>>> res = new HashMap<>(4);
        Future<List<Map<String, Object>>> dev = asyncTask.getList("dev", sql);
        Future<List<Map<String, Object>>> test = asyncTask.getList("test", sql);
        Future<List<Map<String, Object>>> testtjd = asyncTask.getList("testtjd", sql);
        Future<List<Map<String, Object>>> pro = asyncTask.getList("pro", sql);
        res.put("dev", dev.get());
        res.put("test", test.get());
        res.put("testtjd", testtjd.get());
        res.put("pro", pro.get());
        return res;
    }

    protected Map<String, Object> queryForMap(String env, String sql) {
        return mysqlService.queryForMap(env, sql);
    }

    protected <T> T queryForObject(String env, String sql, Class<T> clazz) throws Exception {
        return mysqlService.queryForObject(env, sql, clazz);
    }

    protected int update(String env, String sql) {
        return mysqlService.update(env, sql);
    }

    protected <T> List<T> find(String env, Query query, Class<T> entityClass, String collectionName) {
        return mongoService.find(env, query, entityClass, collectionName);
    }

    protected <T> T insert(String env, T objectToSave, String collectionName) {
        return mongoService.insert(env, objectToSave, collectionName);
    }


    /**
     * 根据SQL语句查询各个环境的结果，并返回比较后结果
     *
     * @param sql
     * @param key
     * @return
     */
    protected List<Map<String, Object>> getCompareResultMapList(String sql, String key, boolean showAll) throws ExecutionException, InterruptedException {
        Map<String, List<Map<String, Object>>> mapOfResult = queryForListFromAll(sql);
        // 排序用列表
        List<String> orderList = new ArrayList<>(mapOfResult.get("dev").size());

        // 所有数据的索引map
        Map<String, Map<String, Object>> map = new HashMap<>(mapOfResult.get("dev").size());
        addListToMap(orderList, key, map, mapOfResult.get("dev"), "dev");
        addListToMap(orderList, key, map, mapOfResult.get("test"), "test");
        addListToMap(orderList, key, map, mapOfResult.get("testtjd"), "testtjd");
        addListToMap(orderList, key, map, mapOfResult.get("pro"), "pro");

        // 处理结果集
        List<Map<String, Object>> resList = new ArrayList<>(orderList.size());
        for (int i = 0; i < orderList.size(); i++) {
            Map<String, Object> dto = map.get(orderList.get(i));
            if (showAll) {
                resList.add(dto);
            } else {
                if (!dto.containsKey("dev") || !dto.containsKey("test")
                        || !dto.containsKey("testtjd") || !dto.containsKey("pro")) {
                    resList.add(dto);
                }
            }
        }
        return resList;
    }

    public void addListToMap(List<String> orderList, String key, Map<String, Map<String, Object>> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> m = list.get(i);
            String k = String.valueOf(m.get(key));
            if (map.containsKey(k)) {
                Map<String, Object> dto = map.get(k);
                dto.put(env, "1");
            } else {
                m.put(env, "1");
                map.put(k, m);
                orderList.add(k);
            }
        }
    }
}
