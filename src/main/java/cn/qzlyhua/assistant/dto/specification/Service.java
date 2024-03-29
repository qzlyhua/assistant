package cn.qzlyhua.assistant.dto.specification;

import cn.hutool.core.collection.CollUtil;
import com.deepoove.poi.plugin.highlight.HighlightRenderData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 服务
 *
 * @author yanghua
 */
@Data
@Builder
public class Service {
    private String title;
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务中文名称
     */
    private String serviceNick;

    /**
     * 服务所属版本
     */
    private String version;

    /**
     * 业务领域
     */
    private String businessArea;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 接口说明
     */
    private String explain;

    /**
     * 入参
     */
    private List<Parameter> reqParameters;
    private String reqExampleStr;
    private HighlightRenderData reqExample;
    private boolean needReq;

    /**
     * 出参
     */
    private List<Parameter> resParameters;
    private String resExampleStr;
    private HighlightRenderData resExample;
    private boolean needRes;

    /**
     * 数据字典
     */
    private List<DictionaryTable> dictionaries;

    public String getTitle() {
        return serviceName + "（" + serviceNick + "）";
    }

    public boolean isNeedReq() {
        return !CollUtil.isEmpty(reqParameters);
    }

    public boolean isNeedRes() {
        return !CollUtil.isEmpty(resParameters);
    }
}
