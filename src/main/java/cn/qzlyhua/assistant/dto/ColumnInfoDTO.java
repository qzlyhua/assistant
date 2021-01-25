package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class ColumnInfoDTO {
    private String tableSchema;
    private String tableName;
    private String columnName;
    private String isNullable;
    private String columnType;
    private String columnKey;
    private String columnComment;
}
