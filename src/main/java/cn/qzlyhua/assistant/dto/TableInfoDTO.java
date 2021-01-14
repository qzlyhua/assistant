package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class TableInfoDTO {
    private String sysName;
    private String versionOfDev;
    private String versionOfStandard;
    private Integer hasUpdate;
    private String htmlUrlOfDev;
    private String htmlUrlOfStandard;
    private String downloadUrl;
}
