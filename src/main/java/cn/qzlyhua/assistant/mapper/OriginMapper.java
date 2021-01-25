package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.Origin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Mapper
public interface OriginMapper {
    @Select("select * from AS_RE_ORIGIN")
    List<Origin> getAllOrigins();

    @Select("SELECT env_type,origin_code,origin_name FROM AS_RE_ORIGIN")
    List<Origin> getOriginsForCompare();

    @Cacheable(value = {"OriginEnv"}, key = "#origin")
    @Select("SELECT env_type from AS_RE_ORIGIN where origin_code = #{origin}")
    String getEnvByOriginCode(String origin);

    @Cacheable(value = {"OriginApiAddress"}, key = "#origin")
    @Select("SELECT REPLACE (address,'oms','api/refreshRoute') address FROM AS_RE_ORIGIN WHERE origin_code = #{origin}")
    String getAddressByOriginCode(String origin);
}
