package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.entity.ServiceCheckEntity;
import com.shenbianys.assisant.entity.ServiceListEntity;
import com.shenbianys.assisant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 服务清单比较（fw_qd表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class FwqdCompareController extends BaseController {
    @RequestMapping("/fwqd/{all}")
    @ResponseBody
    public List<Map<String, Object>> getFwqdInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(fwbh, ';', fwmc, ';', dbbh, ';', bbh))) as md5," +
                " CONCAT(fwbh, '_', fwmc, '_', dbbh, '_', bbh) AS `key`," +
                " fwbh, fwmc, CONCAT(dbbh, '-', bbh) as bbh, fwsm, DATE_FORMAT(xgsj, '%Y-%m-%d %T') as xgsj" +
                " FROM fw_qd ORDER BY xgsj desc, fwmc ASC";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }

    /**
     * 服务清单复制：将 fwmc 服务从 dev 复制到 targetEnv
     *
     * @param env
     * @param key
     * @return
     * @throws Exception
     */
    @RequestMapping("/fwqd/sync/{env}/{key}")
    @ResponseBody
    public JSONObject add(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("执行服务清单数据复制：查询条件 {}，目标环境 {}", key, env);
        JSONObject res = new JSONObject();

        // 校验目标库是否已经存在该服务
        String sql = "SELECT count(id) as c FROM fw_qd where CONCAT(fwbh, '_', fwmc, '_', dbbh, '_', bbh) = '" + key + "'";
        Map<String, Object> mapOfTar = queryForMap(env, sql);
        if (Integer.valueOf(mapOfTar.get("c").toString()) > 0) {
            log.info("执行服务复制取消，目标库已存在该服务");
            log.info("==================================================");
            res.put("result", "error");
            res.put("message", "目标已存在相同数据");
            return res;
        }

        // 校验源库是否存在可复制的数据对象
        Map<String, Object> mapOfSou = queryForMap("dev", sql);
        if (Integer.valueOf(mapOfSou.get("c").toString()) == 0) {
            log.info("执行服务复制取消，源库不存在该服务");
            log.info("==================================================");
            res.put("result", "error");
            res.put("message", "源不存在，操作失败");
            return res;
        }

        // 查询源库的服务清单数据
        String selectQdSql = "select * from fw_qd where CONCAT(fwbh, '_', fwmc, '_', dbbh, '_', bbh) = '" + key + "' order by xgsj desc limit 1";
        ServiceListEntity serviceListEntity = queryForObject("dev", selectQdSql, ServiceListEntity.class);

        if (!"已审核".equals(serviceListEntity.getShzt())) {
            log.info("执行服务复制取消，该服务未审核");
            log.info("==================================================");
            res.put("result", "cancel");
            res.put("message", "服务尚未审核，无法复制！");
            return res;
        }

        // 查询源库的服务审核数据
        String selectShSql = "select * from fw_sh where fwqdid = '" + serviceListEntity.getId() + "' and shzt = '通过' order by cjsj desc limit 1";
        ServiceCheckEntity serviceCheckEntity = queryForObject("dev", selectShSql, ServiceCheckEntity.class);

        // 查询源库的服务模式数据
        Criteria serviceListId = Criteria.where("serviceListId").is(serviceListEntity.getId());
        List<JSONObject> servicePatternList = find("dev", Query.query(serviceListId), JSONObject.class, "ServicePattern");

        // 修改serviceListEntity属性（两者的业务领域和标签基础数据需要保持一致）
        serviceListEntity.setXgrbh("-1");
        serviceListEntity.setXgrxm("robot");
        serviceListEntity.setFwzt("未使用");
        serviceListEntity.setXgrz("从dev环境复制");
        serviceListEntity.setXgsj(new Date());

        // 向目标库插入服务清单数据
        String insertQdSql = SqlUtils.generatorInsertSql(serviceListEntity);
        log.info("向目标库插入服务清单数据：{}", insertQdSql);
        update(env, insertQdSql);

        // 向目标库插入服务审核数据
        String insertShSql = SqlUtils.generatorInsertSql(serviceCheckEntity);
        log.info("向目标库插入服务审核数据：{}", insertShSql);
        update(env, insertShSql);

        // 向目标Mongo库插入服务模式数据
        log.info("向目标Mongo库插入服务模式数据：{}", servicePatternList.get(0));
        insert(env, servicePatternList.get(0), "ServicePattern");

        res.put("result", "success");
        res.put("sqlOfFwqd", insertQdSql);
        res.put("sqlOfFwsh", insertShSql);
        res.put("fwqd", serviceListEntity);
        res.put("fwsh", serviceCheckEntity);
        res.put("fwms", servicePatternList);

        log.info("执行[{}]服务复制完成", key);
        log.info("==================================================");
        return res;
    }
}
