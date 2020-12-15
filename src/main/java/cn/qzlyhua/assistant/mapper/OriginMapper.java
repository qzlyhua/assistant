package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.Origin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.aspectj.weaver.ast.Or;

import java.util.List;

@Mapper
public interface OriginMapper {
    @Select("select * from AS_RE_ORIGIN")
    List<Origin> getAllOrigins();

    @Select("SELECT DISTINCT env_type,'0' AS origin_code,CONCAT(env_type,'共享域') AS origin_name FROM AS_RE_ORIGIN\n" +
            "UNION ALL\n" +
            "SELECT env_type,origin_code,origin_name FROM AS_RE_ORIGIN")
    List<Origin> getOriginsForCompare();
}
