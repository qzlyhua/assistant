package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class ColumnInfoDiffDTO {
    private String table;
    private String column;
    private String isNullableS;
    private String columnTypeS;
    private String columnKeyS;
    private String columnCommentS;
    private String isNullableD;
    private String columnTypeD;
    private String columnKeyD;
    private String columnCommentD;
    private String dShow;
    private String sShow;
}
