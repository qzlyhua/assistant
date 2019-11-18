package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author yangh@winning.com.cn
 */
@Data
@Table("jc_yljg")
public class OrgEntity {
    private String jgbh;
    private String sjjgbh;
    private String jgmc;
    private Date xgsj;
    private String xgrbh;
    private String xgrxm;
//    private String stamp;
}
