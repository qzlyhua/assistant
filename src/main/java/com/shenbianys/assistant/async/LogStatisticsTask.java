package com.shenbianys.assistant.async;

import com.shenbianys.assistant.entity.FwdytjEntity;
import com.shenbianys.assistant.service.MongoService;
import com.shenbianys.assistant.service.impl.MysqlServiceImpl;
import com.shenbianys.assistant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
        // 待统计对象
        String conditionsSql = "SELECT DISTINCT(CONCAT( jgbh, '@', fwmc, '@', jgmc )) as tjdx FROM `fw_ly` " +
                "UNION all SELECT CONCAT( jgbh, '@', fwmc, '@', jgmc ) " +
                "FROM(SELECT DISTINCT jgbh, jgmc FROM fw_ly WHERE jgbh != '0' ) t1 JOIN ( SELECT fwmc FROM fw_ly WHERE jgbh = '0' ) t2";
        List<Map<String, Object>> conditionsList = mysqlService.queryForList(env, conditionsSql);

        // 已完成统计的对象
        String alreadyDoneSql = "select CONCAT(yhybh, '@', fwmc, '@', yhymc) from xt_fwdytj where tjsj = '" + day + "'";
        List<Map<String, Object>> alreadyDoneList = mysqlService.queryForList("dev", alreadyDoneSql);
        Set<String> alreadyDoneConditions = new HashSet<>(alreadyDoneList == null ? 0 : alreadyDoneList.size());

        for (int i = 0; i < alreadyDoneList.size(); i++) {
            alreadyDoneConditions.add((String) alreadyDoneList.get(i).get("condition"));
        }

        List<FwdytjEntity> fwdytjEntitiesToInsert = new ArrayList<>(100);

        for (int i = 0; i < conditionsList.size(); i++) {
            String processing = i + "/" + conditionsList.size();
            Map<String, Object> map = conditionsList.get(i);
            String tjdx = (String) map.get("tjdx");
            String[] tjdxxx = tjdx.split("@");

            if (alreadyDoneConditions.contains(tjdx)) {
                log.info("{}\t用户域：{}({})，{} 方法调用次数已统计", processing, tjdxxx[2], tjdxxx[0], tjdxxx[1]);
                continue;
            }

            long times = countByYhybhAndFwmc(env, tjdxxx[0], tjdxxx[1], day);
            log.info("{}\t用户域：{}({})，{} 方法调用次数：{}", processing, tjdxxx[0], tjdxxx[2], tjdxxx[1], times);

            if (times > 0) {
                FwdytjEntity fwdytjEntity = new FwdytjEntity();
                fwdytjEntity.setDycs(times);
                fwdytjEntity.setFwmc(tjdxxx[1]);
                fwdytjEntity.setYhybh(tjdxxx[0]);
                fwdytjEntity.setYhymc(tjdxxx[2]);
                fwdytjEntity.setTjsj(day);

                fwdytjEntitiesToInsert.add(fwdytjEntity);

                if (fwdytjEntitiesToInsert.size() == 100) {
                    saveAndClear("dev", fwdytjEntitiesToInsert);
                }
            }
        }

        if (fwdytjEntitiesToInsert.size() > 0) {
            saveAndClear("dev", fwdytjEntitiesToInsert);
        }
    }

    private long countByYhybhAndFwmc(String env, String yhybh, String serviceName, String day) throws ParseException {
        String dbName = day.substring(0, 7).replace("-", "_");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = sdf.parse(day + " 00:00:00");

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d1);
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();

        Criteria criteria = Criteria.where("serviceName").is(serviceName)
                .and("orgCode").is(yhybh)
                .and("requestTime").gte(d1).lt(d2);
        Long times = mongoService.count(env, Query.query(criteria), "RequestLog_" + dbName);
        return times;
    }

    private int saveAndClear(String env, List<FwdytjEntity> fwdytjEntities) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        log.info("保存结果集（{} 条数据）", fwdytjEntities.size());
        String sql = SqlUtils.generatorInsertSql(fwdytjEntities) + " ON DUPLICATE KEY UPDATE dycs = VALUES(dycs)";
        int res = mysqlService.update(env, sql);
        fwdytjEntities.clear();
        log.info("执行结束，Affected rows: {}", res);
        return res;
    }
}
