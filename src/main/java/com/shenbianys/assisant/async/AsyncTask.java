package com.shenbianys.assisant.async;

import com.shenbianys.assisant.service.impl.MysqlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 异步执行各个环境的SQL查询
 *
 * @author Yang Hua
 */
@Component
public class AsyncTask {
    @Autowired
    private MysqlServiceImpl mysqlService;

    @Async
    public Future<List<Map<String, Object>>> getList(String env, String sql) {
        List<Map<String, Object>> list = mysqlService.queryForList(env, sql);
        return new AsyncResult<>(list);
    }
}
