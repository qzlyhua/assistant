package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.DbInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yanghua
 */
@Mapper
public interface DbInfoMapper {
    @Select("select * from AS_DB_INFO")
    List<DbInfo> getAllDbInfos();
}
