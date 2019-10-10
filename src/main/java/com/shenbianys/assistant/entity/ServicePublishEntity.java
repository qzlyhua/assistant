package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_fb")
public class ServicePublishEntity {
    @Id
    private String fwfbbh;
    private String fwqdid;
    private String fwbb;
    private String jgbh;
    private String jgmc;
    private String fwbh;
    private String fwmc;
    private String yylx;
    private String ywly;
    private String ywlymc;
    private String fwmsbh;
    private String fwmsmc;
    private String xgrbh;
    private String xgrxm;
    private Date xgsj;
}
