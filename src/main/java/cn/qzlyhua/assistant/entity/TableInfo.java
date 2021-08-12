package cn.qzlyhua.assistant.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author yanghua
 */
@Data
@Builder
public class TableInfo {
    private String name;
    private Date version;
}
