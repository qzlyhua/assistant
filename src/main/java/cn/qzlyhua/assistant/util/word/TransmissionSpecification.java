package cn.qzlyhua.assistant.util.word;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author yanghua
 */
@Data
@Builder
public class TransmissionSpecification {
    private String path;
    private String name;
    private String description;
    private String remarks;
    private List<TransmissionSpecificationParam> reqParams;
    private String reqParamsExample;
    private List<TransmissionSpecificationParam> resParams;
    private String resParamsExample;
    private String version;
    private String businessArea;
    private String businessSubArea;
}
