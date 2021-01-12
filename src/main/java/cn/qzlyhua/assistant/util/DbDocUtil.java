package cn.qzlyhua.assistant.util;

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

    public static void documentGeneration(
            String url, String username, String password,
            String db, String path, EngineFileType fileType, String version, String description) {

        String fileName = db.replace("standard_db_", "") + "-" + version;

        //数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
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
}
