package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_ywly")
public class BusinessAreaEntity {
    @Id
    private String ywlybh;
    private String ywlymc;
    private Date xgsj;
    private String xgrbh;
    private String xgrxm;
    private Integer ywlyjb;
    private String sjywlybh;
    private String yylx;
    private String ywlyqc;
    private String ywlyqc_id;
}
