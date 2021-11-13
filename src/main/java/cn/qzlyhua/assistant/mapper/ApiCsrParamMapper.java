package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.dto.csr.Recommendation;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiCsrParamMapper<selectAllByDescribeLike> {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiCsrParam record);

    int insertSelective(ApiCsrParam record);

    ApiCsrParam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApiCsrParam record);

    int updateByPrimaryKey(ApiCsrParam record);

    int batchInsert(@Param("list") List<ApiCsrParam> list);

    int deleteByCsrId(@Param("csrId") Integer csrId);

    List<ApiCsrParam> selectByCsrIdAndParameterType(@Param("csrId") Integer csrId, @Param("parameterType") String parameterType);

    List<Recommendation> getParamKeysRecommendation(@Param("query") String query, @Param("limit") Integer limit);
}