package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.dto.ColumnInfoDTO;
import cn.qzlyhua.assistant.entity.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yanghua
 */
@Mapper
public interface SchemaMapper {
    /**
     * 获取开发环境标准库信息
     *
     * @return
     */
    @Select("SELECT TABLE_SCHEMA as name, MAX( CREATE_TIME ) AS version FROM " +
            "(SELECT create_time, table_schema FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA LIKE #{tNameSearch}) t " +
            "GROUP BY TABLE_SCHEMA order by version desc")
    List<TableInfo> getStandardTables(String tNameSearch);

    @Select({
            "<script>",
            "SELECT TABLE_SCHEMA as name, MAX( CREATE_TIME ) AS version FROM ",
            "(SELECT create_time, table_schema FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA in ",
            "<foreach collection='ts' item='t' open='(' separator=',' close=')'>#{t}</foreach>",
            ") t GROUP BY TABLE_SCHEMA order by version desc",
            "</script>"
    })
    List<TableInfo> getTableInfos(@Param("ts") List<String> ts);

    @Select("select table_schema, table_name, column_name, is_nullable, column_type, column_key, column_comment " +
            "from information_schema.`COLUMNS` where table_schema = #{db1} and table_name not in('schame_version','WORKER_NODE', 'flyway_schema_history') " +
            "union all " +
            "select table_schema, table_name, column_name, is_nullable, column_type, column_key, column_comment " +
            "from information_schema.`COLUMNS` where table_schema = #{db2} and table_name not in('schame_version','WORKER_NODE', 'flyway_schema_history')")
    List<ColumnInfoDTO> getCloumnInfos(String db1, String db2);
}
