package cn.qzlyhua.assistant.util.word;

import lombok.Builder;
import lombok.Data;

/**
 * 出入参字典
 *
 * @author yanghua
 */
@Data
@Builder
public class Dictionary {
    private String type;
    private String code;
    private String name;
}
