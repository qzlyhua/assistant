package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.ApiCsrDic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiCsrDicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsrDic record);

    int insertSelective(ApiCsrDic record);

    ApiCsrDic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsrDic record);

    int updateByPrimaryKey(ApiCsrDic record);

    int batchInsert(@Param("list") List<ApiCsrDic> list);

    int deleteByTypeAndCode(@Param("type") String type, @Param("code") String code);

    List<ApiCsrDic> selectAllByType(@Param("type") String type);

    List<ApiCsrDic> selectAllByCsrVersion(@Param("version") String version);

    List<ApiCsrDic> selectAllByCsrBusinessArea(@Param("area") String area);
}