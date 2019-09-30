package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.async.AsyncTask;
import com.shenbianys.assisant.service.MongoService;
import com.shenbianys.assisant.service.impl.MysqlServiceImpl;
import com.shenbianys.assisant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 基础Controller-提供通用方法
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class BaseController {
    @Autowired
    private MysqlServiceImpl mysqlService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    protected AsyncTask asyncTask;

    /**
     * MySQL 的查询列表方法
     *
     * @param env
     * @param sql
     * @return
     */
    protected List<Map<String, Object>> queryForList(String env, String sql) {
        return mysqlService.queryForList(env, sql);
    }

    /**
     * 异步查询各环境数据，queryForList的异步调用
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

    /**
     * MySQL 的单条记录查询方法
     *
     * @param env
     * @param sql
     * @return
     */
    protected Map<String, Object> queryForMap(String env, String sql) {
        return mysqlService.queryForMap(env, sql);
    }

    /**
     * MySQL 的单条记录查询方法 - 对Map的进一步封装，自动转成目标类
     *
     * @param env
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    protected <T> T queryForObject(String env, String sql, Class<T> clazz) throws Exception {
        return mysqlService.queryForObject(env, sql, clazz);
    }

    /**
     * MySQL 的 UPDATE 或 INSERT 方法
     *
     * @param env
     * @param sql
     * @return
     */
    protected int update(String env, String sql) {
        log.info("向 {} 环境执行数据库操作：{}", env, sql);
        return mysqlService.update(env, sql);
    }

    protected int update(String env, Object entity) throws IntrospectionException, IllegalAccessException, ParseException, InvocationTargetException {
        String insertSql = SqlUtils.generatorInsertSql(entity);
        log.info("向 {} 环境执行插入操作：{}", env, insertSql);
        return mysqlService.update(env, insertSql);
    }

    /**
     * MongoDB 的查询方法
     *
     * @param env
     * @param query
     * @param entityClass
     * @param collectionName
     * @param <T>
     * @return
     */
    protected <T> List<T> find(String env, Query query, Class<T> entityClass, String collectionName) {
        return mongoService.find(env, query, entityClass, collectionName);
    }

    /**
     * MongoDB 的插入方法
     *
     * @param env
     * @param objectToSave
     * @param collectionName
     * @param <T>
     * @return
     */
    protected <T> T insert(String env, T objectToSave, String collectionName) {
        return mongoService.insert(env, objectToSave, collectionName);
    }

    /**
     * 根据SQL语句查询各个环境的结果，并返回比较后结果List
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
            if (showAll || !dto.containsKey("dev") || !dto.containsKey("test")
                    || !dto.containsKey("testtjd") || !dto.containsKey("pro")) {
                resList.add(dto);
            }
        }
        return resList;
    }

    /**
     * 根据指定的key，将列表内的数据添加到MAP集合。同时建立排序列表，用于最终排序展示
     *
     * @param orderList
     * @param key
     * @param map
     * @param list
     * @param env
     */
    protected void addListToMap(List<String> orderList, String key, Map<String, Map<String, Object>> map, List<Map<String, Object>> list, String env) {
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

    protected int count(String env, String sql) {
        Assert.hasText(sql, "SQL 语句不能为空！");
        Assert.isTrue(sql.toLowerCase().contains("count") && sql.toLowerCase().contains("as c from"), "SQL 不合法");
        Map<String, Object> map = queryForMap(env, sql);
        return map == null ? 0 : Integer.valueOf(map.get("c").toString());
    }

    protected <T> T selectByKeyFromDev(String tableName, String condition, String key, Class<T> clazz) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ").append(tableName);
        sql.append(" where ").append(condition).append(" = '").append(key).append("' limit 1");
        return queryForObject("dev", sql.toString(), clazz);
    }

    protected int countByKey(String env, String tableName, String condition, String key) {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) as c from ").append(tableName);
        sql.append(" where ").append(condition).append(" = '").append(key);
        Map<String, Object> map = queryForMap(env, sql.toString());
        return map == null ? 0 : Integer.valueOf(map.get("c").toString());
    }
}