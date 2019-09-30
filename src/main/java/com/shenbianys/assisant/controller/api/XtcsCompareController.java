package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.controller.api.response.StandardResponse;
import com.shenbianys.assisant.entity.SystemParamConfig;
import com.shenbianys.assisant.util.IdUtils;
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
 * 系统参数比较（xt_ywcs表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class XtcsCompareController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(appcode, '_', csmc)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/xtcs/{all}")
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT appcode, csmc, csms, DATE_FORMAT(xgsj, '%Y-%m-%d %T') as xgsj, " + SQL_KEY_SELECT +
                " from xt_ywcs where jgbh = '001' order by xgsj desc";
        return getCompareResultMapList(sql, "appcode", "all".equals(all));
    }

    @RequestMapping("/xtcs/sync/{env}/{key}")
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "系统参数");

        // 根据 KEY 获取源数据
        String sql = "select * from xt_ywcs where jgbh = '001' and " + SQL_KEY_CONDITION + " = '" + key + "' limit 1";
        SystemParamConfig entity = queryForObject("dev", sql, SystemParamConfig.class);
        Assert.notNull(entity, "源不存在，操作失败");

        // 校验目标环境是否重复
        String countByAppcode = "select count(*) as c from xt_ywcs where jgbh = '001' and " + SQL_KEY_CONDITION
                + " = '" + key + "'";
        int count = count(env, countByAppcode);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 修改数据后执行插入操作
        entity.setXgsj(new Date());
        entity.setCsid(IdUtils.generator());
        int res = update(env, entity);

        // 查询所有需要新增参数的其他用户域
        String sqlForJgbhs = "select DISTINCT jgbh, jgmc from xt_ywcs where jgbh not in('001', '0')";
        List<Map<String, Object>> jgbhList = queryForList(env, sqlForJgbhs);
        for (int i = 0; i < jgbhList.size(); i++) {
            Map<String, Object> m = jgbhList.get(i);
            String jgbh = String.valueOf(m.get("jgbh"));
            String jgmc = String.valueOf(m.get("jgmc"));
            // 校验目标环境是否重复
            String countByAppcodeAndJgbh = "select count(*) as c from xt_ywcs where jgbh = '" + jgbh
                    + "' and " + SQL_KEY_CONDITION + " = '" + key + "'";
            if (count(env, countByAppcodeAndJgbh) == 0) {
                entity.setJgbh(jgbh);
                entity.setJgmc(jgmc);
                entity.setCsid(IdUtils.generator());
                res += update(env, entity);
            }
        }
        return res;
    }
}
