package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author yangh@winning.com.cn
 */
@Data
@Table("jc_xzqh")
public class AreaEntity {
    private String xzqhbh;
    private String sjqhbh;
    private String xzqhmc;
    private String xzqhqc;
    private String xzqhlx;
    private Date xgsj;
    private String xgrbh;
    private String xgrxm;
    private String stamp;
    private Boolean sfmj;
}
