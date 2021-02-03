package cn.qzlyhua.assistant.dto.specification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 章节
 *
 * @author yanghua
 */
@Data
@Builder
public class Chapter {
    /**
     * 章节标题
     */
    private String headWord;

    /**
     * 服务列表
     */
    private List<Service> services;
}
