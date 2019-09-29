package com.shenbianys.assisant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("xt_ywcs")
public class SystemParamConfig {
    @Id
    private String csid;
    private String csmc;
    private String csms;
    private String csqz;
    private String appcode;
    private String appname;
    private String jgbh;
    private String jgmc;
    private String srfs;
    private String cszy;
    private String xgrbh;
    private String xgrxm;
    private Date xgsj;
}
