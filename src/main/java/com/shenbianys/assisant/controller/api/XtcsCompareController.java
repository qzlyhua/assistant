package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.entity.SystemParamConfig;
import com.shenbianys.assisant.util.IdUtils;
import com.shenbianys.assisant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 系统参数比较（xt_ywcs表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class XtcsCompareController extends BaseController {
    @RequestMapping("/xtcs/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT appcode,appcode AS `key`,csmc,csms,DATE_FORMAT(xgsj, '%Y-%m-%d %T') as xgsj" +
                " from xt_ywcs where jgbh = '001' order by xgsj desc";
        return getCompareResultMapList(sql, "appcode", "all".equals(all));
    }

    @RequestMapping("/xtcs/sync/{env}/{key}")
    @ResponseBody
    public JSONObject sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("开始执行数据复制：目标环境 {}，查询条件 {}", env, key);
        JSONObject res = new JSONObject();

        String sql = "select * from xt_ywcs where jgbh = '001' and appcode = '" + key + "'";
        SystemParamConfig entity = queryForObject("dev", sql, SystemParamConfig.class);

        if (entity == null) {
            res.put("result", "error");
            res.put("message", "源不存在，操作失败");
            return res;
        } else {
            // 校验是否重复
            String sqlCheck = "select count(*) as c from xt_ywcs where jgbh = '001' and appcode = '" + entity.getAppcode() + "'";
            Map<String, Object> map = queryForMap(env, sqlCheck);
            if (Integer.valueOf(map.get("c").toString()) > 0) {
                res.put("result", "error");
                res.put("message", "目标已存在相同数据");
                return res;
            }

            // 执行插入操作
            entity.setXgsj(new Date());
            String insertSql = SqlUtils.generatorInsertSql(entity);
            log.info("向目标库插入数据：{}", insertSql);
            update(env, insertSql);

            String sqlForJgbhs = "select DISTINCT jgbh, jgmc from xt_ywcs where jgbh not in('001', '0')";
            List<Map<String, Object>> jgbhList = queryForList(env, sqlForJgbhs);
            for (int i = 0; i < jgbhList.size(); i++) {
                Map<String, Object> m = jgbhList.get(i);
                entity.setJgbh(String.valueOf(m.get("jgbh")));
                entity.setJgmc(String.valueOf(m.get("jgmc")));
                entity.setCsid(IdUtils.generator());
                String s = SqlUtils.generatorInsertSql(entity);
                log.info("向目标库插入数据：{}", s);
                update(env, s);
            }
            log.info("执行[{}]系统参数复制完成", key);
            log.info("==================================================");

            res.put("result", "success");
            return res;
        }
    }
}
