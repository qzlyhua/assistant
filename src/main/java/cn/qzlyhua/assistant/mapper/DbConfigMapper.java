package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.DbConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author yanghua
 */
@Mapper
public interface DbConfigMapper {
    @Select("select * from AS_CFG_DB where env_type=#{envType} and db_type=#{dbType} and db_schema=#{dbSchema} ")
    DbConfig getDbConfig(String envType, String dbType, String dbSchema);
}
