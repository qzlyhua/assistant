package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ZipUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.TableInfoDTO;
import cn.qzlyhua.assistant.entity.TableInfo;
import cn.qzlyhua.assistant.service.SchemaService;
import cn.qzlyhua.assistant.util.DbDocUtil;
import cn.smallbun.screw.core.engine.EngineFileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * MySQL数据库操作
 *
 * @author yanghua
 */
@RestController
@Response
@Slf4j
@RequestMapping("/api")
public class MySqlSchemaController {
    @Resource
    SchemaService schemaService;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${app.db.doc.path}")
    private String basePath;

    /**
     * 比较两个数据库的区别
     *
     * @param db1
     * @param db2
     * @return
     */
    @RequestMapping("/compare/{db1}/{db2}")
    public String getDbDocs(@PathVariable String db1, @PathVariable String db2) {
        String flag = "mysql://";
        String temp = url.substring(url.indexOf(flag) + flag.length(), url.indexOf("?"));
        String ipAndPort = temp.substring(0, temp.indexOf("/"));
        String sql = schemaService.getDiff(username, password, ipAndPort, db1, db2);
        String ignore = "# WARNING: Using a password on the command line interface can be insecure.";
        return sql.replace(ignore, "").replaceAll("\\n", " \\\r\\\n");
    }

    /**
     * 获取所有数据库内配置的系统数据库更新情况
     *
     * @return
     */
    @RequestMapping("/getTableInfos")
    public List<TableInfoDTO> getTableInfos() {
        return schemaService.getTableInfos();
    }

    /**
     * 下载word文件
     *
     * @param dbSchema
     * @param response
     * @throws IOException
     */
    @RequestMapping("/doc/{dbSchema}")
    public void downloadDoc(@PathVariable String dbSchema, HttpServletResponse response) throws IOException {
        TableInfo t = schemaService.getStandardTable(dbSchema);
        String v = DatePattern.PURE_DATETIME_FORMAT.format(t.getVersion());
        String db = t.getName();
        String des = "标准库-" + db.replace("standard_db_", "");

        String myDbName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        String dbUrl = url.replace(myDbName, dbSchema);
        String wordFilePath = DbDocUtil.documentGeneration(dbUrl, username, password, db, basePath + "/word", EngineFileType.WORD, v, des);

        log.info("开始将文件{}写入到输出流...", wordFilePath);
        File word = new File(wordFilePath);
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=" + FileUtil.getName(word));
        ServletOutputStream out = response.getOutputStream();
        FileReader.create(word).writeToStream(out, true);
    }

    /**
     * 打开html页面
     *
     * @param dbSchema
     * @param response
     * @throws IOException
     */
    @RequestMapping("/html/{dbSchema}")
    public void downloadHtml(@PathVariable String dbSchema, HttpServletResponse response) throws IOException {
        TableInfo t = schemaService.getStandardTable(dbSchema);
        String v = DatePattern.PURE_DATETIME_FORMAT.format(t.getVersion());
        String db = t.getName();
        String des = db + "-" + v;

        String myDbName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        String dbUrl = url.replace(myDbName, dbSchema);
        String htmlFilePath = DbDocUtil.documentGeneration(dbUrl, username, password, db, basePath + "/html", EngineFileType.HTML, v, des);

        log.info("开始将文件{}写入到输出流...", htmlFilePath);
        File html = new File(htmlFilePath);
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Content-Disposition", "inline;filename=" + FileUtil.getName(html));
        ServletOutputStream out = response.getOutputStream();
        FileReader.create(html).writeToStream(out, true);
    }

    /**
     * 打包下载数据库文档文件（包括html和word）
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/docs")
    public void downloadAll(HttpServletResponse response) throws IOException {
        String myDbName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        List<TableInfo> tables = schemaService.getStandardTables("standard_db%");
        FileUtil.clean(basePath);

        for (TableInfo t : tables) {
            String v = DatePattern.PURE_DATETIME_FORMAT.format(t.getVersion());
            String db = t.getName();
            String des = "标准库-" + db.replace("standard_db_", "");
            String dbUrl = url.replace(myDbName, db);
            DbDocUtil.documentGeneration(dbUrl, username, password, db, basePath + "/word", EngineFileType.WORD, v, des);
            DbDocUtil.documentGeneration(dbUrl, username, password, db, basePath + "/html", EngineFileType.HTML, v, des);
        }

        log.info("开始压缩{}文件夹", basePath);
        File file = ZipUtil.zip(basePath);

        log.info("开始将文件{}写入到输出流...", FileUtil.getName(file));
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + FileUtil.getName(file));
        ServletOutputStream out = response.getOutputStream();
        FileReader.create(file).writeToStream(out, true);
    }
}
