package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.qzlyhua.assistant.controller.api.exception.AppException;
import cn.qzlyhua.assistant.controller.api.response.ResponseCode;
import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.entity.DbConfig;
import cn.qzlyhua.assistant.mapper.DbConfigMapper;
import cn.qzlyhua.assistant.mapper.OriginMapper;
import cn.qzlyhua.assistant.service.DbManager;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据库操作
 *
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

    @Resource
    OriginMapper originMapper;

    @Override
    public int selectRouteConfigCount(String originCode) throws SQLException {
        String env = originMapper.getEnvByOriginCode(originCode);
        String sql = "select count(1) as c from (" +
                "SELECT wgly,yhy FROM xt_ly WHERE zfbz=0 AND yhy='0' AND wgly NOT IN (" +
                "SELECT wgly FROM xt_ly WHERE zfbz=0 AND yhy= ?) UNION ALL " +
                "SELECT wgly,yhy FROM xt_ly WHERE zfbz=0 AND yhy= ? " +
                ")t";
        Db db = getDbByConfig(env, "mysql", "oms");
        List<Entity> list = db.query(sql, originCode, originCode);
        return list.get(0).getInt("c");
    }

    @Override
    public List<RouteConfigDetail> getRouteConfigDetails(String originCode) throws SQLException {
        String env = originMapper.getEnvByOriginCode(originCode);
        String sql = "select wgly, hddz, hdly, yhy from (" +
                "SELECT wgly, yhy, hddz, hdly FROM xt_ly WHERE zfbz=0 AND yhy='0' AND wgly NOT IN (" +
                "SELECT wgly FROM xt_ly WHERE zfbz=0 AND yhy= ?) UNION ALL " +
                "SELECT wgly, yhy, hddz, hdly FROM xt_ly WHERE zfbz=0 AND yhy= ?" +
                ")t order by yhy, hddz, wgly";
        Db db = getDbByConfig(env, "mysql", "oms");
        List<Entity> list = db.query(sql, originCode, originCode);
        List<RouteConfigDetail> result = new ArrayList<>(list.size());
        for (Entity e : list) {
            RouteConfigDetail rcd = new RouteConfigDetail();
            rcd.setRoute(e.getStr("wgly"));
            rcd.setApplication(e.getStr("hddz").replace(originCode, ""));
            rcd.setService(e.getStr("hdly"));
            rcd.setOrigin(e.getStr("yhy"));
            result.add(rcd);
        }
        return result;
    }

    @Override
    public RouteConfigDetail getRouteConfigByCRoute(String env, String originCode, String route) throws SQLException {
        env = StrUtil.isBlank(env) ? originMapper.getEnvByOriginCode(originCode) : env;
        String sql = "select wgly, hddz, hdly, yhy from xt_ly where zfbz = 0 and yhy = ? and wgly = ? limit 1";
        Db db = getDbByConfig(env, "mysql", "oms");
        List<Entity> list = db.query(sql, originCode, route);
        if (CollUtil.isEmpty(list)) {
            return null;
        }

        Entity e = list.get(0);
        RouteConfigDetail rcd = new RouteConfigDetail();
        rcd.setRoute(e.getStr("wgly"));
        rcd.setApplication(e.getStr("hddz").replace(originCode, ""));
        rcd.setService(e.getStr("hdly"));
        rcd.setOrigin(e.getStr("yhy"));
        return rcd;
    }

    @Override
    public int insertRouteConfig(String originCode, RouteConfigDetail routeConfigDetail) throws SQLException {
        String env = originMapper.getEnvByOriginCode(originCode);
        Db db = getDbByConfig(env, "mysql", "oms");
        String oriCode = routeConfigDetail.getOrigin().equals("0") ? "0" : originCode;
        Entity routeConfig = new Entity("xt_ly");
        routeConfig.set("id", getNextIdOfRouteConfig(env));
        routeConfig.set("wgly", routeConfigDetail.getRoute());
        routeConfig.set("hdly", routeConfigDetail.getService());
        routeConfig.set("jqlx", 0);
        routeConfig.set("ssjgbh", null);
        routeConfig.set("ssjgmc", null);
        routeConfig.set("yhy", oriCode);
        routeConfig.set("hddz", getApplicationName(env, oriCode, routeConfigDetail.getApplication()));
        routeConfig.set("cjrid", -2);
        routeConfig.set("cjrxm", "assistant");
        routeConfig.set("cjsj", new Date());
        routeConfig.set("xgrid", -2);
        routeConfig.set("xgrxm", "assistant");
        routeConfig.set("xgsj", new Date());
        routeConfig.set("xgsj", new Date());
        routeConfig.set("zfbz", 0);
        routeConfig.set("qyjy", 1);
        return db.insert(routeConfig);
    }

    private long getNextIdOfRouteConfig(String env) throws SQLException {
        String sql = "SELECT IFNULL(min(id)-1, 10000) FROM xt_ly";
        Db db = getDbByConfig(env, "mysql", "oms");
        Number number = db.queryNumber(sql);
        return number.longValue();
    }

    private String getApplicationName(String env, String originCode, String application) throws SQLException {
        String sql = "select hddz from xt_ly where zfbz = 0 and yhy = ? and hddz like ? limit 1";
        Db db = getDbByConfig(env, "mysql", "oms");
        String appName = db.queryString(sql, originCode, application + "%");
        return appName == null ? application : appName;
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
