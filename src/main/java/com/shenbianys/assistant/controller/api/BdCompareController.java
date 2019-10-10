package com.shenbianys.assistant.controller.api;

import com.shenbianys.assistant.controller.api.response.StandardResponse;
import com.shenbianys.assistant.entity.FormEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 表单列表比较（bd_bd表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class BdCompareController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(bdbh, '_', bdmc, '_' ,yylx)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/bd/{all}")
    public List<Map<String, Object>> data(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT bdbh, " + SQL_KEY_SELECT + ", bdmc, yylx FROM bd_bd order by xgsj desc";
        return getCompareResultMapList(sql, "bdbh", "all".equals(all));
    }

    @RequestMapping("/bd/sync/{env}/{key}")
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "表单列表");

        // 根据 KEY 获取源数据
        FormEntity entity = selectByKeyFromDev("bd_bd", SQL_KEY_CONDITION, key, FormEntity.class);
        Assert.notNull(entity, "源不存在，操作失败");

        // 根据 KEY 判断目标数据库是否存在重复数据
        int count = countByKey(env, "bd_bd", SQL_KEY_CONDITION, key);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 修改数据
        entity.setXgsj(new Date());

        // 执行插入操作
        int result = update(env, entity);
        Assert.isTrue(result == 1, "操作失败");
        return result;
    }
}
