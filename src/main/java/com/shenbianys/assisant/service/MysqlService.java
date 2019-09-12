package com.shenbianys.assisant.service;

import java.util.List;
import java.util.Map;

/**
 * @author Yang Hua
 */
public interface MysqlService {
    List<Map<String, Object>> queryForList(String env, String sql);

    <T> T queryForObject(String env, String sql, Class<T> clazz) throws Exception;

    Map<String, Object> queryForMap(String env, String sql) throws Exception;

    int update(String env, String sql);
}
