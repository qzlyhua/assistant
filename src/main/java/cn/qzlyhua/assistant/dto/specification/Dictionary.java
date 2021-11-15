package cn.qzlyhua.assistant.dto.specification;

import lombok.Builder;
import lombok.Data;

/**
 * 数据字典
 *
 * @author yanghua
 */
@Data
@Builder
public class Dictionary {
    private String code;
    private String name;
}
