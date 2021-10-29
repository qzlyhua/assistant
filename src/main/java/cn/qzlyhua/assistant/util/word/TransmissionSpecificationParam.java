package cn.qzlyhua.assistant.util.word;

import lombok.Builder;
import lombok.Data;

/**
 * @author yanghua
 */
@Data
@Builder
public class TransmissionSpecificationParam {
    private String key;
    private String type;
    private String describe;
    private String required;
}
