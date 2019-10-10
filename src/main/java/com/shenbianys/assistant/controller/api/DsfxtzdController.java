package com.shenbianys.assistant.controller.api;

import com.shenbianys.assistant.controller.api.response.StandardResponse;
import com.shenbianys.assistant.entity.ThirdSystemInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 第三方系统字典比较（xt_dsfxtzd表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class DsfxtzdController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(zdid, '_', xtbs, '_', xtmc)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/dsfxtzd/{all}")
    public List<Map<String, Object>> data(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT MD5(CONCAT(zdid,';',xtbs,';',xtmc)) as md5, " + SQL_KEY_SELECT +
                ", zdid, xtbs, xtmc, yhbdms, qqms, sfblxx FROM xt_dsfzd order by zdid";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    @RequestMapping("/dsfxtzd/sync/{env}/{key}")
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "系统字典");

        // 根据 KEY 获取源数据
        ThirdSystemInfoEntity entity = selectByKeyFromDev("xt_dsfzd", SQL_KEY_CONDITION, key, ThirdSystemInfoEntity.class);
        Assert.notNull(entity, "源不存在，操作失败");

        // 根据 KEY 判断目标数据库是否存在重复数据
        int count = countByKey(env, "xt_dsfzd", SQL_KEY_CONDITION, key);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 执行插入操作
        int result = update(env, entity);
        Assert.isTrue(result == 1, "操作失败");
        return result;
    }
}
