package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.entity.TablesInfo;
import cn.qzlyhua.assistant.service.SchemaService;
import cn.qzlyhua.assistant.util.DbDocUtil;
import cn.smallbun.screw.core.engine.EngineFileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author yanghua
 */
@RestController
@Response
public class DbController {
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

    @RequestMapping("/db/docs")
    public void getDbDocs(HttpServletResponse response) throws IOException {
        List<TablesInfo> tables = schemaService.getStandardTables();
        String zipFilePath = basePath.substring(0, basePath.lastIndexOf("/")) + "/db_doc_zip.zip";

        for (TablesInfo t : tables) {
            String v = DatePattern.PURE_DATETIME_FORMAT.format(t.getVersion());
            String db = t.getName();
            String des = "标准库-" + db.replace("standard_db_", "");

            DbDocUtil.documentGeneration(url, username, password, db, basePath + "/word", EngineFileType.WORD, v, des);
            DbDocUtil.documentGeneration(url, username, password, db, basePath + "/html", EngineFileType.HTML, v, des);
        }

        File file = ZipUtil.zip(basePath, zipFilePath);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=db_doc.zip");
        ServletOutputStream out = response.getOutputStream();
        FileUtil.writeToStream(file, out);
        IoUtil.close(out);
    }
}
