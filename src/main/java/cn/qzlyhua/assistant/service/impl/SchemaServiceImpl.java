package cn.qzlyhua.assistant.service.impl;

import cn.qzlyhua.assistant.entity.TablesInfo;
import cn.qzlyhua.assistant.mapper.SchemaMapper;
import cn.qzlyhua.assistant.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yanghua
 */
@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {
    @Resource
    SchemaMapper schemaMapper;

    @Override
    public List<TablesInfo> getStandardTables() {
        String search = "standard_db%";
        log.info("开始获取所有标准库信息");
        List<TablesInfo> list = schemaMapper.getStandardTables(search);
        log.info("标准库信息：{}", list);
        return list;
    }
}
