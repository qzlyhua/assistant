package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.controller.api.response.StandardResponse;
import com.shenbianys.assisant.entity.ServiceCheckEntity;
import com.shenbianys.assisant.entity.ServiceListEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 服务清单比较（fw_qd表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class FwqdCompareController extends BaseController {
    private static final String SQL_KEY_CONDITION = "CONCAT(fwbh, '_', fwmc, '_', dbbh, '_', bbh)";
    private static final String SQL_KEY_SELECT = SQL_KEY_CONDITION + " AS `key`";

    @RequestMapping("/fwqd/{all}")
    public List<Map<String, Object>> data(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(fwbh, ';', fwmc, ';', dbbh, ';', bbh))) as md5," + SQL_KEY_SELECT +
                ", fwbh, fwmc, CONCAT(dbbh, '-', bbh) as bbh, fwsm, DATE_FORMAT(xgsj, '%Y-%m-%d %T') as xgsj" +
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
    public int sync(@PathVariable String env, @PathVariable String key) throws Exception {
        log.info("以关键条件 [{}] 向目标环境 [{}] 执行 [{}] 数据复制", key, env, "服务清单");

        // 根据 KEY 获取源数据
        ServiceListEntity serviceListEntity = selectByKeyFromDev("fw_qd", SQL_KEY_CONDITION, key, ServiceListEntity.class);
        Assert.notNull(serviceListEntity, "源不存在，操作失败");
        Assert.isTrue("已审核".equals(serviceListEntity.getShzt()), "未审核的服务");

        // 根据 KEY 判断目标数据库是否存在重复数据
        int count = countByKey(env, "fw_qd", SQL_KEY_CONDITION, key);
        Assert.isTrue(count == 0, "目标已存在相同数据");

        // 查询源库的服务审核数据
        String selectShSql = "select * from fw_sh where fwqdid = '" + serviceListEntity.getId() + "' and shzt = '通过' order by cjsj desc limit 1";
        ServiceCheckEntity serviceCheckEntity = queryForObject("dev", selectShSql, ServiceCheckEntity.class);
        Assert.notNull(serviceCheckEntity, "服务审核数据缺失");

        // 查询源库的服务模式数据
        Criteria serviceListId = Criteria.where("serviceListId").is(serviceListEntity.getId());
        List<JSONObject> servicePatternList = find("dev", Query.query(serviceListId), JSONObject.class, "ServicePattern");
        Assert.notEmpty(servicePatternList, "服务模式数据缺失");

        // 修改serviceListEntity属性（两者的业务领域和标签基础数据需要保持一致）
        serviceListEntity.setXgrbh("-1");
        serviceListEntity.setXgrxm("robot");
        serviceListEntity.setFwzt("未使用");
        serviceListEntity.setXgrz("从dev环境复制");
        serviceListEntity.setXgsj(new Date());

        // 向目标库插入服务清单数据
        update(env, serviceListEntity);

        // 向目标库插入服务审核数据
        update(env, serviceCheckEntity);

        // 向目标Mongo库插入服务模式数据
        log.info("执行插入操作：{}", servicePatternList.get(0));
        insert(env, servicePatternList.get(0), "ServicePattern");

        return 1;
    }
}
