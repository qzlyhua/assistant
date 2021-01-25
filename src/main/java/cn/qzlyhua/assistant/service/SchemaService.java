package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.ColumnInfoDiffDTO;
import cn.qzlyhua.assistant.dto.TableInfoDTO;
import cn.qzlyhua.assistant.entity.TableInfo;

import java.util.List;

/**
 * 库表结构管理
 *
 * @author yanghua
 */
public interface SchemaService {
    TableInfo getStandardTable(String schema);

    List<TableInfo> getStandardTables(String search);

    String getDiff(String user, String password, String ipAndPort, String db1, String db2);

    List<TableInfoDTO> getTableInfos();

    List<ColumnInfoDiffDTO> getCloumnInfoDiffs(String db1, String db2);
}
