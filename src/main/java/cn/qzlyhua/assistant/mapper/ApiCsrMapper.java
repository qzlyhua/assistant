package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.dto.csr.GroupByBusinessArea;
import cn.qzlyhua.assistant.dto.csr.GroupByVersion;
import cn.qzlyhua.assistant.entity.ApiCsr;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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

    List<String> selectBusinessAreaByUpdateTimeAfter(@Param("minUpdateTime") Date minUpdateTime);

    List<String> selectBusinessAreaByVersion(@Param("version")String version);

    List<ApiCsr> selectAll();

    List<GroupByBusinessArea> statisticsByBusinessArea();

    List<GroupByVersion> statisticsByVersion();
}