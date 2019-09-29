package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.entity.FormEntity;
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
 * 表单比较（bd_bd表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class BdCompareController extends BaseController {
    @RequestMapping("/bd/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT bdbh, bdbh AS `key`, bdmc, yylx FROM bd_bd order by xgsj desc";
        return getCompareResultMapList(sql, "bdbh", "all".equals(all));
    }

    @RequestMapping("/bd/sync/{env}/{key}")
    @ResponseBody
    public JSONObject sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("执行表单列表数据复制：查询条件 {}，目标环境 {}", key, env);
        JSONObject res = new JSONObject();

        String sql = "select * from bd_bd where bdbh = '" + key + "' limit 1";
        FormEntity entity = queryForObject("dev", sql, FormEntity.class);

        if (entity == null) {
            res.put("result", "error");
            res.put("message", "源不存在，操作失败");
            return res;
        } else {
            // 校验是否重复
            String sqlCheckBd = "select count(*) as c from bd_bd where bdbh = '" + entity.getBdbh() + "'";
            Map<String, Object> map = queryForMap(env, sqlCheckBd);
            if (Integer.valueOf(map.get("c").toString()) > 0) {
                res.put("result", "error");
                res.put("message", "目标已存在相同数据");
                return res;
            }

            // 执行插入操作
            entity.setXgsj(new Date());
            String insertSql = SqlUtils.generatorInsertSql(entity);
            log.info("向目标库插入表单列表数据：{}", insertSql);
            update(env, insertSql);
            log.info("执行[{}]表单列表复制完成", key);
            log.info("==================================================");

            res.put("result", "success");
            res.put("sql", insertSql);
            res.put("entity", entity);
            return res;
        }
    }
}
