package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.dto.csr.GroupByBusinessArea;import cn.qzlyhua.assistant.dto.csr.GroupByVersion;import cn.qzlyhua.assistant.entity.ApiCsr;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiCsrMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsr record);

    int insertSelective(ApiCsr record);

    ApiCsr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsr record);

    int updateByPrimaryKey(ApiCsr record);

    int batchInsert(@Param("list") List<ApiCsr> list);

    ApiCsr selectOneByPath(@Param("path") String path);

    List<ApiCsr> selectByVersion(@Param("version") String version);

    List<ApiCsr> selectByBusinessArea(@Param("businessArea") String businessArea);

    List<ApiCsr> selectAll();

    List<GroupByBusinessArea> statisticsByBusinessArea();

    List<GroupByVersion> statisticsByVersion();
}