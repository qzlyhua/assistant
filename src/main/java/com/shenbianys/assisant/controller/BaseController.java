package com.shenbianys.assisant.controller;

import com.shenbianys.assisant.service.MongoService;
import com.shenbianys.assisant.service.impl.MysqlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Controller
public class BaseController {
    @Autowired
    private MysqlServiceImpl mysqlService;

    @Autowired
    private MongoService mongoService;

    protected List<Map<String, Object>> queryForList(String env, String sql) {
        return mysqlService.queryForList(env, sql);
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
}
