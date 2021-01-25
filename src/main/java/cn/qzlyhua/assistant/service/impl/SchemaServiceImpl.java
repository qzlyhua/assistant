package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.qzlyhua.assistant.dto.ColumnInfoDTO;
import cn.qzlyhua.assistant.dto.ColumnInfoDiffDTO;
import cn.qzlyhua.assistant.dto.TableInfoDTO;
import cn.qzlyhua.assistant.entity.DbInfo;
import cn.qzlyhua.assistant.entity.TableInfo;
import cn.qzlyhua.assistant.mapper.DbInfoMapper;
import cn.qzlyhua.assistant.mapper.SchemaMapper;
import cn.qzlyhua.assistant.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 库表结构管理
 *
 * @author yanghua
 */
@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {
    @Resource
    SchemaMapper schemaMapper;

    @Resource
    DbInfoMapper dbInfoMapper;

    @Override
    public TableInfo getStandardTable(String schema) {
        List<TableInfo> list = schemaMapper.getStandardTables(schema);
        return list.get(0);
    }

    @Override
    public List<TableInfo> getStandardTables(String search) {
        log.info("开始获取数据库信息：{}", search);
        List<TableInfo> list = schemaMapper.getStandardTables(search);
        log.info("查询结果：{}", list);
        return list;
    }

    @Override
    public String getDiff(String user, String password, String ipAndPort, String db1, String db2) {
        password = password.replace("@", "\\@");
        String server = user + ":" + password + "@" + ipAndPort;
        String db = db1 + ":" + db2;
        String command = "mysqldiff --server1=" + server + " --server2=" + server + " --difftype=sql --show-reverse --force -q " + db;
        log.info("执行命令：{}", command);

        try {
            String[] cmd = {"sh", "-c", command};
            Process process = Runtime.getRuntime().exec(cmd);

            String is = IoUtil.readUtf8(process.getInputStream());
            String es = IoUtil.readUtf8(process.getErrorStream());

            log.info(is);
            log.error(es);

            process.waitFor();
            process.destroy();
            return StrUtil.isNotBlank(es) ? es : is;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TableInfoDTO> getTableInfos() {
        // 数据库配置的表内容
        List<DbInfo> Dbs = dbInfoMapper.getAllDbInfos();

        // 系统索引：数据库名称-系统名称
        Map<String, DbInfo> indexMap = new HashMap<>();
        List<String> ts = new ArrayList<>();
        for (DbInfo d : Dbs) {
            indexMap.put(d.getDbSchema(), d);
            ts.add(d.getDbSchema());
        }

        // 查询这些表的最近更新时间
        List<TableInfo> tInfos = schemaMapper.getTableInfos(ts);

        // 以系统名称分组，分别处理标准库信息和开发库信息
        Map<String, TableInfoDTO> group = new HashMap<>();
        for (TableInfo d : tInfos) {
            String sysName = indexMap.get(d.getName()).getSysName();
            if (group.containsKey(sysName)) {
                TableInfoDTO tableInfoDTO = group.get(sysName);
                setVersion(tableInfoDTO, d, indexMap.get(d.getName()));
            } else {
                TableInfoDTO tableInfoDTO = new TableInfoDTO();
                tableInfoDTO.setSysName(sysName);
                setVersion(tableInfoDTO, d, indexMap.get(d.getName()));
                group.put(sysName, tableInfoDTO);
            }
        }

        // 返回最终结果
        List<TableInfoDTO> result = new ArrayList<>(group.values());
        for (TableInfoDTO dto : result) {
            boolean b = DateUtil.parse(dto.getVersionOfDev()).isAfter(DateUtil.parse(dto.getVersionOfStandard()));
            dto.setHasUpdate(b ? 1 : 0);
            dto.setCompareUrl("db-doc/compare/" + dto.getSchemaNameOfDev() + "/" + dto.getSchemaNameOfStandard());
        }

        return result;
    }

    private void setVersion(TableInfoDTO tableInfoDTO, TableInfo tableInfo, DbInfo dbInfo) {
        boolean isStandard = "标准库".equalsIgnoreCase(dbInfo.getDbType());
        String version = DateUtil.format(tableInfo.getVersion(), DatePattern.NORM_DATETIME_FORMAT);
        if (isStandard) {
            tableInfoDTO.setVersionOfStandard(version);
            tableInfoDTO.setDownloadUrl("api/doc/" + dbInfo.getDbSchema());
            tableInfoDTO.setHtmlUrlOfStandard("api/html/" + dbInfo.getDbSchema());
            tableInfoDTO.setSchemaNameOfStandard(dbInfo.getDbSchema());
        } else {
            tableInfoDTO.setVersionOfDev(version);
            tableInfoDTO.setHtmlUrlOfDev("api/html/" + dbInfo.getDbSchema());
            tableInfoDTO.setSchemaNameOfDev(dbInfo.getDbSchema());
        }
    }

    @Override
    public List<ColumnInfoDiffDTO> getCloumnInfoDiffs(String db1, String db2) {
        List<ColumnInfoDTO> columnInfoDTOSList = schemaMapper.getCloumnInfos(db1, db2);

        Map<String, ColumnInfoDiffDTO> indexMap = new LinkedHashMap<>(32);
        for (ColumnInfoDTO dto : columnInfoDTOSList) {
            String key = dto.getTableName() + "." + dto.getColumnName();
            if (indexMap.containsKey(key)) {
                ColumnInfoDiffDTO diff = indexMap.get(key);
                processDiff(diff, dto);
            } else {
                ColumnInfoDiffDTO diff = new ColumnInfoDiffDTO();
                diff.setTable(dto.getTableName());
                diff.setColumn(dto.getColumnName());
                processDiff(diff, dto);
                indexMap.put(key, diff);
            }
        }

        // 返回最终结果
        List<ColumnInfoDiffDTO> temp = new ArrayList<>(indexMap.values());
        List<ColumnInfoDiffDTO> result = new ArrayList<>();
        for (ColumnInfoDiffDTO c : temp) {
            boolean isSameIsNull = c.getIsNullableD() != null && c.getIsNullableD().equals(c.getIsNullableS());
            boolean isSameColTyp = c.getColumnTypeD() != null &&  c.getColumnTypeD().equals(c.getColumnTypeS());
            boolean isSameColKey = (StrUtil.isBlank(c.getIsNullableD()) && StrUtil.isBlank(c.getIsNullableS()))
                    || StrUtil.isNotBlank(c.getIsNullableD()) && c.getColumnKeyD().equals(c.getColumnKeyS());
            if (!(isSameIsNull && isSameColTyp && isSameColKey)) {
                c.setDShow(join(" | ", c.getColumnTypeD(), c.getIsNullableD(), c.getColumnKeyD()));
                c.setSShow(join(" | ", c.getColumnTypeS(), c.getIsNullableS(), c.getColumnKeyS()));
                result.add(c);
            }
        }
        return result;
    }

    private void processDiff(ColumnInfoDiffDTO diff, ColumnInfoDTO dto) {
        boolean isStandard = dto.getTableSchema().contains("standard_db");
        if (isStandard) {
            diff.setIsNullableS(dto.getIsNullable());
            diff.setColumnTypeS(dto.getColumnType());
            diff.setColumnKeyS(dto.getColumnKey());
            diff.setColumnCommentS(dto.getColumnComment());
        } else {
            diff.setIsNullableD(dto.getIsNullable());
            diff.setColumnTypeD(dto.getColumnType());
            diff.setColumnKeyD(dto.getColumnKey());
            diff.setColumnCommentD(dto.getColumnComment());
        }
    }

    private String join(CharSequence conjunction, String cType, String isNullable, String cKey) {
        if (StrUtil.isBlank(cType) && StrUtil.isBlank(isNullable) && StrUtil.isBlank(cKey)) {
            return "缺";
        }

        if (StrUtil.isBlank(cKey)) {
            return StrUtil.join(" | ", cType, isNullable);
        } else {
            return StrUtil.join(" | ", cType, isNullable, cKey);
        }
    }
}
