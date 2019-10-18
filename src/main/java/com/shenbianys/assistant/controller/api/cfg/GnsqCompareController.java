package com.shenbianys.assistant.controller.api.cfg;

import com.shenbianys.assistant.controller.api.BaseController;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import com.shenbianys.assistant.entity.SqGnsqEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 功能授权比较（sq_gnsq表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class GnsqCompareController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(id, '_', dm, '_', mc)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/gnsq/{all}")
    public List<Map<String, Object>> data(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(dm,mc,sjdm,lx))) as md5, " + SQL_KEY_SELECT +
                ", id, dm, mc, sjdm, CASE lx WHEN 1 THEN '医生端' ELSE '居民端' END lx FROM sq_gnsq order by lx, dm asc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    @RequestMapping("/gnsq/sync/{env}/{key}")
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "功能授权");

        // 根据 KEY 获取源数据
        SqGnsqEntity entity = selectByKeyFromDev("sq_gnsq", SQL_KEY_CONDITION, key, SqGnsqEntity.class);
        Assert.notNull(entity, "源不存在，操作失败");

        // 根据 KEY 判断目标数据库是否存在重复数据
        int count = countByKey(env, "sq_gnsq", SQL_KEY_CONDITION, key);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 执行插入操作
        int result = update(env, entity);
        Assert.isTrue(result == 1, "操作失败");
        return result;
    }
}
