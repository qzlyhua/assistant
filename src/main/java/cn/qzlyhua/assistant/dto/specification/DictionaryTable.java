package cn.qzlyhua.assistant.dto.specification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 数据字典
 *
 * @author yanghua
 */
@Data
@Builder
public class DictionaryTable {
    private String type;
    private int size;
    private List<Dictionary> dictionaryList;
}
