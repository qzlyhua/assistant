package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.ApiCsrParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiCsrParamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsrParam record);

    int insertSelective(ApiCsrParam record);

    ApiCsrParam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsrParam record);

    int updateByPrimaryKey(ApiCsrParam record);

    int batchInsert(@Param("list") List<ApiCsrParam> list);

    int deleteByCsrId(@Param("csrId")Integer csrId);
}