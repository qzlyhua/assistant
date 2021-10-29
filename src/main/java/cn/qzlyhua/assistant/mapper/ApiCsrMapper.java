package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.ApiCsr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiCsrMapper<selectByPath> {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsr record);

    int insertSelective(ApiCsr record);

    ApiCsr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsr record);

    int updateByPrimaryKey(ApiCsr record);

    int batchInsert(@Param("list") List<ApiCsr> list);

    ApiCsr selectOneByPath(@Param("path")String path);

    List<ApiCsr> selectByVersion(@Param("version")String version);

    List<ApiCsr> selectByBusinessArea(@Param("businessArea")String businessArea);
}