package com.shenbianys.assistant.async;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.entity.FwdytjEntity;
import com.shenbianys.assistant.service.MongoService;
import com.shenbianys.assistant.service.impl.MysqlServiceImpl;
import com.shenbianys.assistant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yang Hua
 */
@Slf4j
@Component
public class LogStatisticsTask {
    @Autowired
    private MysqlServiceImpl mysqlService;

    @Autowired
    private MongoService mongoService;

    /**
     * 统计指定环境的指定日期的日志数据
     *
     * @param env
     * @param day
     */
    @Async
    public void doStatistics(String env, String day) throws ParseException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        String yhydzb = "select DISTINCT jgbh, jgmc from fw_ly";
        List<Map<String, Object>> yhyList = mysqlService.queryForList("pro", yhydzb);
        Map<String, String> map = new HashMap<>(yhyList.size());
        for (int i = 0; i < yhyList.size(); i++) {
            Map<String, Object> yhy = yhyList.get(i);
            map.put(String.valueOf(yhy.get("jgbh")), String.valueOf(yhy.get("jgmc")));
        }

        MongoTemplate mongoTemplate = mongoService.getMongoTemplateByEnv(env);
        String dbName = "RequestLog_" + day.substring(0, 7).replace("-", "_");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = sdf.parse(day + " 00:00:00");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d1);
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();

        MatchOperation match = Aggregation.match(Criteria.where("requestTime").lt(d2).gt(d1));
        GroupOperation group = Aggregation.group("serviceName", "orgCode").count().as("dycs");
        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(match, group), dbName, JSONObject.class);
        List<JSONObject> list = aggregate.getMappedResults();
        List<FwdytjEntity> fwdytjEntitiesToInsert = new ArrayList<>(200);
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = list.get(i);
            FwdytjEntity fwdytjEntity = new FwdytjEntity();
            fwdytjEntity.setDycs(jsonObject.getLong("dycs"));
            fwdytjEntity.setFwmc(jsonObject.getString("serviceName"));
            fwdytjEntity.setYhybh(jsonObject.getString("orgCode"));
            fwdytjEntity.setYhymc(map.get(jsonObject.getString("orgCode")));
            fwdytjEntity.setTjsj(day);

            if (!StringUtils.isEmpty(fwdytjEntity.getFwmc()) && !StringUtils.isEmpty(fwdytjEntity.getYhybh())) {
                fwdytjEntitiesToInsert.add(fwdytjEntity);
            }

            if (fwdytjEntitiesToInsert.size() == 200) {
                saveAndClear("dev", fwdytjEntitiesToInsert);
            }
        }

        if (fwdytjEntitiesToInsert.size() > 0) {
            saveAndClear("dev", fwdytjEntitiesToInsert);
        }

        log.info("日志统计操作完成");
    }

    private int saveAndClear(String env, List<FwdytjEntity> fwdytjEntities) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        log.info("保存结果集（{} 条数据）", fwdytjEntities.size());
        String sql = SqlUtils.generatorInsertSql(fwdytjEntities) + " ON DUPLICATE KEY UPDATE dycs = VALUES(dycs)";
        log.info("执行SQL：{}", sql);
        int res = mysqlService.update(env, sql);
        fwdytjEntities.clear();
        log.info("执行结束，Affected rows: {}", res);
        return res;
    }
}
