package cn.qzlyhua.assistant.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * @author yanghua
 */
public class DbDocUtil {

    static void documentGeneration(String db, EngineFileType fileType, String version, String description) {
        String q = "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&useTimezone=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&autoReconnect=true";
        String url = "jdbc:mysql://192.168.150.103:3306/" + db + q;
        String user = "root";
        String pwd = "ewell@xc";

        String path = "/Users/yanghua/Downloads/db_doc";
        String fileName = db.replace("standard_db_", "") + "-" + version;

        //数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pwd);
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource ds = new HikariDataSource(hikariConfig);

        //生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                .fileOutputDir(path)
                .openOutputDir(false)
                .fileType(fileType)
                .produceType(EngineTemplateType.freemarker)
                .fileName(fileName)
                .build();

        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("flyway_schema_history");
        ignoreTableName.add("WORKER_NODE");

        ProcessConfig processConfig = ProcessConfig.builder()
                .ignoreTableName(ignoreTableName)
                .build();

        Configuration config = Configuration.builder()
                .dataSource(ds)
                .version(version)
                .description(description)
                .engineConfig(engineConfig)
                .produceConfig(processConfig)
                .build();

        //执行
        new DocumentationExecute(config).execute();
    }

    public static void main(String[] args) {
        String[] dbs = new String[]{
                "standard_db_bos", "standard_db_cdm", "standard_db_cms", "standard_db_fds",
                "standard_db_hcr", "standard_db_hms", "standard_db_hrm", "standard_db_hsm",
                "standard_db_ims", "standard_db_mms", "standard_db_oms", "standard_db_pem",
                "standard_db_phs", "standard_db_tms", "standard_db_ums"};

        String v = DatePattern.PURE_DATETIME_FORMAT.format(new DateTime());

        for (String db : dbs) {
            documentGeneration(db, EngineFileType.WORD, "V1.0-" + v, "标准库-" + db.replace("standard_db_", ""));
            documentGeneration(db, EngineFileType.HTML, "V1.0-" + v, "标准库-" + db.replace("standard_db_", ""));
        }
    }
}
