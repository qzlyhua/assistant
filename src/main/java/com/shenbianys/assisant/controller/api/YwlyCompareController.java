package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.entity.BusinessAreaEntity;
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
 * 业务领域比较（fw_ywly表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class YwlyCompareController extends BaseController {
    @RequestMapping("/ywly/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(yylx,ywlyjb,ywlymc))) AS md5, CONCAT(yylx,'_',ywlyjb,'_',ywlymc) AS `key`," +
                " yylx, ywlyjb, ywlymc FROM `fw_ywly` order by yylx, ywlyjb, ywlymc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    @RequestMapping("/ywly/sync/{env}/{key}")
    @ResponseBody
    public JSONObject sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("执行业务领域数据复制：查询条件 {}，目标环境 {}", key, env);
        JSONObject res = new JSONObject();

        String sql = "select * from fw_ywly where CONCAT( yylx, '_', ywlyjb, '_', ywlymc ) = '" + key + "' limit 1";
        BusinessAreaEntity entity = queryForObject("dev", sql, BusinessAreaEntity.class);

        if (entity == null) {
            res.put("result", "error");
            res.put("message", "源不存在，操作失败");
            return res;
        } else {
            // 校验是否重复
            String sqlCheckYwly = "select count(*) as c from fw_ywly where ywlybh = '" + entity.getYwlybh() + "'";
            Map<String, Object> map = queryForMap(env, sqlCheckYwly);
            if (Integer.valueOf(map.get("c").toString()) > 0) {
                res.put("result", "error");
                res.put("message", "目标已存在相同数据");
                return res;
            }

            // 校验上级业务领域数据
            String sqlCheckSjywly = "select * from fw_ywly where ywlybh = '" + entity.getSjywlybh() + "'";
            BusinessAreaEntity entityCheckSjywly = queryForObject(env, sqlCheckSjywly, BusinessAreaEntity.class);
            if (entityCheckSjywly == null) {
                res.put("result", "error");
                res.put("message", "上级业务领域不存在");
                return res;
            }

            // 执行插入操作
            entity.setXgsj(new Date());
            String insertSql = SqlUtils.generatorInsertSql(entity);
            log.info("向目标库插入业务领域数据：{}", insertSql);
            update(env, insertSql);
            log.info("执行[{}]业务领域复制完成", key);
            log.info("==================================================");

            res.put("result", "success");
            res.put("sql", insertSql);
            res.put("entity", entity);
            return res;
        }
    }
}
