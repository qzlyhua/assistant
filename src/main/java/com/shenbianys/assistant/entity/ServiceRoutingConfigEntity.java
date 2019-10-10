package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_ly")
public class ServiceRoutingConfigEntity {
    @Id
    private String lybh;
    private String jgbh;
    private String jgmc;
    private String ip;
    private Date cjsj;
    private String cjrbh;
    private String cjrxm;
    private String yylx;
    private String fwdz;
    private String fwbh;
    private String fwmc;
    private String fwmsbh;
    private String fwmsmc;
    private String xtbh;
    private String xtmc;
    private String cszt;
    private String dsffwmc;
}
