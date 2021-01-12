package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.entity.TablesInfo;

import java.util.List;

/**
 * @author yanghua
 */
public interface SchemaService {
    List<TablesInfo> getStandardTables();
}
