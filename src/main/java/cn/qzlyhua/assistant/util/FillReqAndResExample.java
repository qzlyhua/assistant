package cn.qzlyhua.assistant.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.nosql.mongo.MongoFactory;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * 根据ESB日志内容填充传输规范的出入参举例
 *
 * @author yanghua
 */
@Slf4j
public class FillReqAndResExample {
    private static final String DB_MYSQL_OMS = "my_oms";
    private static final String DB_MONGO_OMS = "group_pro_mongo";
    private static final String DB_MONGO_LOG_COLL = "2021_11_esb_log";
    private static final Integer MONGO_SELECT_ROWS = 10;

    public static void main(String[] args) throws SQLException {
        List<String> reqEmptyServiceNames = getServiceNameWhereReqExampleIsNull();
        for (String sn : reqEmptyServiceNames) {
            List<String> paths = getPathByServiceName(sn);
            for (String p : paths) {
                String req = getReqJsonStr(p);
                if (StrUtil.isNotBlank(req)) {
                    log.info("方法：{}，入参举例：{}", sn, req);
                    Entity where = new Entity("AS_API_CSR").set("path", sn);
                    Entity record = new Entity("AS_API_CSR").set("req_params_example", req);
                    Db.use("my_ass").update(record, where);
                    break;
                }
            }
        }

        List<String> resEmptyServiceNames = getServiceNameWhereResExampleIsNull();
        for (String sn : resEmptyServiceNames) {
            List<String> paths = getPathByServiceName(sn);
            for (String p : paths) {
                String res = getResJsonStr(p);
                if (StrUtil.isNotBlank(res)) {
                    log.info("方法：{}，出参举例：{}", sn, res);
                    Entity where = new Entity("AS_API_CSR").set("path", sn);
                    Entity record = new Entity("AS_API_CSR").set("res_params_example", res);
                    Db.use("my_ass").update(record, where);
                    break;
                }
            }
        }
    }

    /**
     * 获取请求参数示例为空的传输规范
     *
     * @return
     * @throws SQLException
     */
    private static List<String> getServiceNameWhereReqExampleIsNull() throws SQLException {
        String sql = "select path from AS_API_CSR where req_params_example is null and version = 'PP001'";
        List<Entity> entities = Db.use("my_ass").query(sql);
        return entities.stream().map(e -> e.getStr("path")).collect(Collectors.toList());
    }

    /**
     * 获取响应体数据示例为空的传输规范
     *
     * @return
     * @throws SQLException
     */
    private static List<String> getServiceNameWhereResExampleIsNull() throws SQLException {
        String sql = "select path from AS_API_CSR where res_params_example is null and version = 'PP001'";
        List<Entity> entities = Db.use("my_ass").query(sql);
        return entities.stream().map(e -> e.getStr("path")).collect(Collectors.toList());
    }

    /**
     * 根据传输规范方法名获取路由配置表网关路由名（对应最终请求日志表的path属性）
     *
     * @param serviceName
     * @return
     * @throws SQLException
     */
    private static List<String> getPathByServiceName(String serviceName) throws SQLException {
        String sql = "select wgly from xt_ly where wgly like CONCAT('%','" + serviceName + "')";
        List<Entity> entities = Db.use(DB_MYSQL_OMS).query(sql);
        return entities.stream().map(e -> e.getStr("wgly")).collect(Collectors.toList());
    }

    /**
     * 从日志文件获取请求参数（requestParams或requestBody）
     *
     * @param path
     * @return
     */
    private static String getReqJsonStr(String path) {
        log.info("处理请求方法：{}", path);
        MongoDatabase db = MongoFactory.getDS(DB_MONGO_OMS).getDb("oms");
        MongoCollection<Document> requestLogEntities = db.getCollection(DB_MONGO_LOG_COLL);
        for (Document d : requestLogEntities.find(and(eq("path", path))).limit(MONGO_SELECT_ROWS)) {
            if (d.containsKey("requestBody") || d.containsKey("requestParams")) {
                String requestBody = JSONUtil.toJsonStr(d.get("requestBody"));
                String requestParams = JSONUtil.toJsonStr(d.get("requestParams"));
                String req = (StrUtil.isBlank(requestBody) || "{}".equals(requestBody)) ? requestParams : requestBody;
                if (StrUtil.isNotBlank(req) && !"{}".equals(req)) {
                    return req.length() > 1000 ? (req.substring(0, 900) + "……" + req.substring(req.length() - 100)) : req;
                }
            }
        }
        return "";
    }

    /**
     * 从日志文件获取响应参数
     *
     * @param path
     * @return
     */
    private static String getResJsonStr(String path) {
        MongoDatabase db = MongoFactory.getDS(DB_MONGO_OMS).getDb("oms");
        MongoCollection<Document> requestLogEntities = db.getCollection(DB_MONGO_LOG_COLL);
        for (Document d : requestLogEntities
                .find(and(eq("path", path), eq("responseBody.code", 200)))
                .limit(MONGO_SELECT_ROWS)) {
            if (d.containsKey("requestBody") || d.containsKey("requestParams")) {
                String res = JSONUtil.toJsonStr(d.get("responseBody"));
                if (StrUtil.isNotBlank(res) && !"{}".equals(res)) {
                    return res.length() > 1000 ? (res.substring(0, 900) + "……" + res.substring(res.length() - 100)) : res;
                }
            }
        }
        return "";
    }
}
