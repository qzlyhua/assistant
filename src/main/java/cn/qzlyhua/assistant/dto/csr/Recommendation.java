package cn.qzlyhua.assistant.dto.csr;

import lombok.Data;

/**
 * 出入参属性自动完成
 * @author yanghua
 */
@Data
public class Recommendation {
    private String label;
    private String describe;
    private String type;
    private String required;
}
