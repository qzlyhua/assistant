package cn.qzlyhua.assistant.service.impl;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.qzlyhua.assistant.controller.api.exception.AppException;
import cn.qzlyhua.assistant.controller.api.response.ResponseCode;
import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.entity.DbConfig;
import cn.qzlyhua.assistant.mapper.DbConfigMapper;
import cn.qzlyhua.assistant.service.DbManager;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghua
 */
@Service
public class DbManagerImpl implements DbManager {
    private static Map<Integer, Db> dbMap;

    DbManagerImpl() {
        dbMap = new HashMap<>();
    }

    @Resource
    DbConfigMapper dbConfigMapper;

    @Override
    public int selectRouteConfigCount(String env, String originCode) throws SQLException {
        String sql = "select count(*) as c from xt_ly where zfbz = 0 and yhy = ?";
        Db db = getDbByConfig(env, "mysql", "oms");
        List<Entity> list = db.query(sql, originCode);
        return list.get(0).getInt("c");
    }

    @Override
    public List<RouteConfigDetail> getRouteConfigDetails(String env, String originCode) throws SQLException {
        String sql = "select wgly, hddz, hdly from xt_ly where zfbz = 0 and yhy = ? order by wgly";
        Db db = getDbByConfig(env, "mysql", "oms");
        List<Entity> list = db.query(sql, originCode);
        List<RouteConfigDetail> result = new ArrayList<>(list.size());
        for (Entity e : list) {
            RouteConfigDetail rcd = new RouteConfigDetail();
            rcd.setRoute(e.getStr("wgly"));
            rcd.setApplication(e.getStr("hddz").replace(originCode, ""));
            rcd.setService(e.getStr("hdly"));
            result.add(rcd);
        }
        return result;
    }

    private Db getDbByConfig(String env, String dbType, String dbSchema) {
        DbConfig dbConfig = dbConfigMapper.getDbConfig(env, dbType, dbSchema);
        if (dbConfig != null) {
            if (dbMap.containsKey(dbConfig.getId())) {
                return dbMap.get(dbConfig.getId());
            } else {
                DruidDataSource ds = new DruidDataSource();
                ds.setUrl(dbConfig.getUrl());
                ds.setUsername(dbConfig.getUser());
                ds.setPassword(dbConfig.getPwd());
                Db db = Db.use(ds);
                dbMap.put(dbConfig.getId(), db);
                return db;
            }
        } else {
            throw new AppException(ResponseCode.DB_CONFIG_ERROR);
        }
    }
}
