package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.controller.api.response.StandardResponse;
import com.shenbianys.assisant.entity.BusinessAreaEntity;
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
 * 业务领域比较（fw_ywly表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class YwlyCompareController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(yylx, '_', ywlyjb, '_' ,ywlymc)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/ywly/{all}")
    public List<Map<String, Object>> data(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(yylx,ywlyjb,ywlymc))) AS md5, " + SQL_KEY_SELECT +
                ", yylx, ywlyjb, ywlymc FROM `fw_ywly` order by yylx, ywlyjb, ywlymc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    @RequestMapping("/ywly/sync/{env}/{key}")
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "业务领域");

        // 根据 KEY 获取源数据
        BusinessAreaEntity entity = selectByKeyFromDev("fw_ywly", SQL_KEY_CONDITION, key, BusinessAreaEntity.class);
        Assert.notNull(entity, "源不存在，操作失败");

        // 根据 KEY 判断目标数据库是否存在重复数据
        int count = countByKey(env, "fw_ywly", SQL_KEY_CONDITION, key);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 校验上级业务领域数据
        if (entity.getSjywlybh() != null) {
            String countBySjywlybh = "select count(*) as c from fw_ywly where ywlybh = '" + entity.getSjywlybh() + "'";
            int countBySjywlybhResult = count(env, countBySjywlybh);
            Assert.isTrue(countBySjywlybhResult > 0, "上级业务领域不存在");
        }

        // 修改数据
        entity.setXgsj(new Date());

        // 执行插入操作
        int result = update(env, entity);
        Assert.isTrue(result == 1, "操作失败");
        return result;
    }
}
