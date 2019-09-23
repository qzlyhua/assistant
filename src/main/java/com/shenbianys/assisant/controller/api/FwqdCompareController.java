package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.dto.FwqdCompareDTO;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务清单比较（fw_qd表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class FwqdCompareController extends BaseController {
    @RequestMapping("/fwqd/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareFw(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT fwmc, fwbh, fwsm FROM fw_qd ORDER BY fwmc ASC";
        List<Map<String, Object>> listA = queryForList(envA, sql);
        List<Map<String, Object>> listB = queryForList(envB, sql);
        Map<String, Map<String, Object>> a2b = getCompareResultOfFw(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOfFw(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(envA + "有" + envB + "没有", a2b);
        res.put(envB + "有" + envA + "没有", b2a);
        return res;
    }

    private Map<String, Map<String, Object>> getCompareResultOfFw(
            List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            String fwmc = String.valueOf(listA.get(i).get("fwmc"));
            map.put(fwmc, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String fwmc = String.valueOf(listB.get(i).get("fwmc"));
            map.remove(fwmc);
        }

        return map;
    }

    @RequestMapping("/fwqd/add/{sourceEnv}/{fwmc}/{targetEnv}")
    @ResponseBody
    public JSONObject add(@PathVariable String sourceEnv, @PathVariable String fwmc, @PathVariable String targetEnv) throws Exception {
        log.info("执行[{}]服务复制：从{}环境复制到{}环境", fwmc, sourceEnv, targetEnv);
        JSONObject res = new JSONObject();

        // 校验目标库是否已经存在该服务
        String sql = "SELECT count(id) as c FROM fw_qd where fwmc = '" + fwmc + "'";
        Map<String, Object> mapOfTar = queryForMap(targetEnv, sql);
        if (Integer.valueOf(mapOfTar.get("c").toString()) > 0) {
            log.info("执行[{}]服务复制取消，目标库已存在该服务", fwmc);
            log.info("==================================================");
            res.put("result", "cancel");
            res.put("message", "目标库已存在" + fwmc + "服务");
            return res;
        }

        // 校验源库是否存在可复制的数据对象
        Map<String, Object> mapOfSou = queryForMap(sourceEnv, sql);
        if (Integer.valueOf(mapOfSou.get("c").toString()) == 0) {
            log.info("执行[{}]服务复制取消，源库不存在该服务", fwmc);
            log.info("==================================================");
            res.put("result", "cancel");
            res.put("message", "源库不存在" + fwmc + "服务，无法复制");
            return res;
        }

        // 查询源库的服务清单数据
        String selectQdSql = "select * from fw_qd where fwmc = '" + fwmc + "' order by xgsj desc limit 1";
        ServiceListEntity serviceListEntity = queryForObject(sourceEnv, selectQdSql, ServiceListEntity.class);

        if (!"已审核".equals(serviceListEntity.getShzt())){
            log.info("执行[{}]服务复制取消，该服务未审核", fwmc);
            log.info("==================================================");
            res.put("result", "cancel");
            res.put("message", fwmc + "服务尚未审核，无法复制！");
            return res;
        }

        // 查询源库的服务审核数据
        String selectShSql = "select * from fw_sh where fwqdid = '" + serviceListEntity.getId() + "' and shzt = '通过' order by cjsj desc limit 1";
        ServiceCheckEntity serviceCheckEntity = queryForObject(sourceEnv, selectShSql, ServiceCheckEntity.class);

        // 查询源库的服务模式数据
        Criteria serviceListId = Criteria.where("serviceListId").is(serviceListEntity.getId());
        List<JSONObject> servicePatternList = find(sourceEnv, Query.query(serviceListId), JSONObject.class, "ServicePattern");

        // 修改serviceListEntity属性（两者的业务领域和标签基础数据需要保持一致）
        serviceListEntity.setXgrbh("-1");
        serviceListEntity.setXgrxm("robot");
        serviceListEntity.setFwzt("未使用");
        serviceListEntity.setXgrz("从" + sourceEnv + "环境复制");
        serviceListEntity.setXgsj(new Date());

        // 向目标库插入服务清单数据
        String insertQdSql = SqlUtils.generatorInsertSql(serviceListEntity);
        log.info("向目标库插入服务清单数据：{}", insertQdSql);
        update(targetEnv, insertQdSql);

        // 向目标库插入服务审核数据
        String insertShSql = SqlUtils.generatorInsertSql(serviceCheckEntity);
        log.info("向目标库插入服务审核数据：{}", insertShSql);
        update(targetEnv, insertShSql);

        // 向目标Mongo库插入服务模式数据
        log.info("向目标Mongo库插入服务模式数据：{}", servicePatternList.get(0));
        insert(targetEnv, servicePatternList.get(0), "ServicePattern");

        res.put("result", "success");
        res.put("message", fwmc + "服务从" + sourceEnv + "环境复制到" + targetEnv + "环境完成");
        res.put("sqlOfFwqd", insertQdSql);
        res.put("sqlOfFwsh", insertShSql);
        res.put("fwqd", serviceListEntity);
        res.put("fwsh", serviceCheckEntity);
        res.put("fwms", servicePatternList);

        log.info("执行[{}]服务复制完成", fwmc);
        log.info("==================================================");
        return res;
    }

    @RequestMapping("/fwqd/{all}")
    @ResponseBody
    public List<FwqdCompareDTO> getFwqdInfo(@PathVariable String all) {
        String sql = "SELECT MD5(CONCAT(fwbh , \"_\" , fwmc)) as md5, fwbh, fwmc, dbbh, bbh, fwsm, xgsj FROM fw_qd ORDER BY xgsj desc, fwmc ASC";
        List<Map<String, Object>> listDev = queryForList("dev", sql);
        List<Map<String, Object>> listTest = queryForList("test", sql);
        List<Map<String, Object>> listTesttjd = queryForList("testtjd", sql);
        List<Map<String, Object>> listPro = queryForList("pro", sql);

        Map<String, FwqdCompareDTO> map = new HashMap<>(listDev.size());
        for (int i = 0; i < listDev.size(); i++) {
            String md5 = String.valueOf(listDev.get(i).get("md5"));
            map.put(md5, new FwqdCompareDTO("dev", listDev.get(i)));
        }

        addListToMap(map, listTest, "test");
        addListToMap(map, listTesttjd, "testtjd");
        addListToMap(map, listPro, "pro");
        List<FwqdCompareDTO> resList = map.values().stream().collect(Collectors.toList());

        // 移除各环境已经都配置的数据
        if (!"all".equals(all)) {
            for (Iterator<FwqdCompareDTO> iter = resList.listIterator(); iter.hasNext(); ) {
                FwqdCompareDTO dto = iter.next();
                if (dto.isAllSet()) {
                    iter.remove();
                }
            }
        }
        return resList;
    }

    private void addListToMap(Map<String, FwqdCompareDTO> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            String md5 = String.valueOf(list.get(i).get("md5"));
            if (map.containsKey(md5)) {
                FwqdCompareDTO dto = map.get(md5);
                dto.setEnv(env);
            } else {
                map.put(md5, new FwqdCompareDTO(env, list.get(i)));
            }
        }
    }
}
