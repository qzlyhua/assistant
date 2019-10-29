package com.shenbianys.assistant.controller.api.bind;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import com.shenbianys.assistant.controller.api.BaseController;
import com.shenbianys.assistant.util.HsbUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 医生绑定功能
 *
 * @author yangh@winning.com.cn
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class DoctorBindController extends BaseController {
    @RequestMapping("/{env}/bind")
    public JSONArray bind(@PathVariable String env, @RequestParam String yhms,
                          @RequestParam String systemId, @RequestParam String key) {
        JSONArray bindResult = new JSONArray();

        log.info("使用医生的 {} 作为第三方系统 {} 的绑定用户名", key, systemId);
        log.info("待绑定用户名:{}", yhms);

        // 获取第三方系统信息
        String getDsfxtSql = "SELECT id,xtbs,xtmc FROM `xt_dsfxt` where id = '" + systemId + "'";
        Map<String, Object> dsfxt = queryForMap(env, getDsfxtSql);

        // 获取医生信息
        String getDoctorUrl = "ysxx?page=1&size=1&accountType=3&username=";

        String[] yhmList = yhms.split(",");
        for (String yhm : yhmList) {
            JSONObject res = HsbUtils.getFromHsb(env, getDoctorUrl + yhm);
            int size = res.getIntValue("totalNumber");
            if (1 == size) {
                JSONObject doctor = res.getJSONArray("data").getJSONObject(0);

                boolean alreadyBind = false;
                JSONArray doctorBinding = doctor.getJSONArray("binding");
                for (int i = 0; i < doctorBinding.size(); i++) {
                    JSONObject o = doctorBinding.getJSONObject(i);
                    if (dsfxt.get("id").equals(o.getString("thirdPartySystemId"))) {
                        log.info("{} 医生已绑定 {} 系统", yhm, o.getString("systemIdentification"));
                        alreadyBind = true;
                        break;
                    }
                }

                if (alreadyBind) {
                    continue;
                }

                // idCard 或者 username
                String bingdingName = "admin".equals(key) ? "admin" : doctor.getString(key);
                if (StringUtils.isEmpty(bingdingName)) {
                    log.info("{} 医生 {} 为空", yhm, key);
                    continue;
                }

                JSONObject result = doBinding(env, dsfxt, doctor, bingdingName);
                bindResult.add(result);
            } else {
                log.info("{} 医生不存在", yhm);
            }
        }

        return bindResult;
    }

    private JSONObject doBinding(String env, Map<String, Object> dsfxt, JSONObject doctor, String bingdingName) {
        log.info("绑定 {} 医生 {} 用户名：{}", doctor.getString("username"), dsfxt.get("xtmc"), bingdingName);

        JSONObject req = new JSONObject();
        req.put("id", "");
        req.put("doctorId", doctor.getLongValue("id"));
        req.put("doctorUsername", doctor.getString("username"));

        req.put("thirdPartySystemId", dsfxt.get("id"));
        req.put("systemIdentification", dsfxt.get("xtbs"));
        req.put("username", bingdingName);

        return HsbUtils.postForHsb(env, "bddsfxt", req);
    }

    @RequestMapping("/getYhys")
    public List<Map<String, Object>> getYhys() {
        String sql = "SELECT distinct jgbh, jgmc FROM `xt_dsfxt` where jgbh != '0' order by jgbh";
        return queryForList("pro", sql);
    }

    @RequestMapping("/getDsfxts/{jgbh}")
    public List<Map<String, Object>> getDsfxts(@PathVariable String jgbh) {
        String sql = "SELECT id, xtmc FROM `xt_dsfxt` where jgbh = '" + jgbh + "' and user_binding_pattern = 1 order by xtbs";
        return queryForList("pro", sql);
    }

}
