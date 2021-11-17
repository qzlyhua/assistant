package cn.qzlyhua.assistant.util;

import cn.hutool.db.Db;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author yanghua
 */
public class DruidDataSourceUtils {
    public static Db getDb(String url, String username, String password) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        Db db = Db.use(ds);

        return db;
    }
}
