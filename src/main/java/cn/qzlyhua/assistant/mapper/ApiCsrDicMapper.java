package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.ApiCsrDic;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiCsrDicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsrDic record);

    int insertSelective(ApiCsrDic record);

    ApiCsrDic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsrDic record);

    int updateByPrimaryKey(ApiCsrDic record);

    int batchInsert(@Param("list") List<ApiCsrDic> list);

    int deleteByTypeAndCode(@Param("type")String type,@Param("code")String code);

    List<ApiCsrDic> selectAllByType(@Param("type")String type);


}