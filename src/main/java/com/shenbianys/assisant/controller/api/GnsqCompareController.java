package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.entity.BusinessAreaEntity;
import com.shenbianys.assisant.entity.SqGnsqEntity;
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
 * 功能授权比较（sq_gnsq表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class GnsqCompareController extends BaseController {
    @RequestMapping("/gnsq/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(dm,mc,sjdm,lx))) as md5, CONCAT(id, '_', dm, '_', mc) AS `key`, " +
                "id, dm, mc, sjdm, CASE lx WHEN 1 THEN '医生端' ELSE '居民端' END lx FROM sq_gnsq order by lx, dm asc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    @RequestMapping("/gnsq/sync/{env}/{key}")
    @ResponseBody
    public JSONObject sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("执行功能授权数据复制：查询条件 {}，目标环境 {}", key, env);
        JSONObject res = new JSONObject();

        String sql = "select * from sq_gnsq where CONCAT(id, '_', dm, '_', mc) = '" + key + "' limit 1";
        SqGnsqEntity entity = queryForObject("dev", sql, SqGnsqEntity.class);

        if (entity == null) {
            res.put("result", "error");
            res.put("message", "源不存在，操作失败");
            return res;
        } else {
            // 校验是否重复
            String sqlCheckYwly = "select count(*) as c from sq_gnsq where id = '" + entity.getId() + "'";
            Map<String, Object> map = queryForMap(env, sqlCheckYwly);
            if (Integer.valueOf(map.get("c").toString()) > 0) {
                res.put("result", "error");
                res.put("message", "目标已存在相同数据");
                return res;
            }

            // 执行插入操作
            String insertSql = SqlUtils.generatorInsertSql(entity);
            log.info("向目标库插入功能授权数据：{}", insertSql);
            update(env, insertSql);
            log.info("执行[{}]功能授权复制完成", key);
            log.info("==================================================");

            res.put("result", "success");
            res.put("sql", insertSql);
            res.put("entity", entity);
            return res;
        }
    }
}
