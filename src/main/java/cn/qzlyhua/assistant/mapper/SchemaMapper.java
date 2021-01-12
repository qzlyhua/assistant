package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.TablesInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yanghua
 */
@Mapper
public interface SchemaMapper {
    /**
     * 获取开发环境标准库信息
     * @return
     */
    @Select("SELECT TABLE_SCHEMA as name, MAX( CREATE_TIME ) AS version FROM " +
            "(SELECT create_time, table_schema FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA LIKE #{tNameSearch}) t " +
            "GROUP BY TABLE_SCHEMA order by version desc")
    List<TablesInfo> getStandardTables(String tNameSearch);
}
