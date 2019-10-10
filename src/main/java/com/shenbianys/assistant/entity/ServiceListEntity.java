package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_qd")
public class ServiceListEntity {
    @Column("id")
    private String id;
    private Date xgsj;
    private String xgrbh;
    private String xgrxm;
    private String yylx;
    private String ywlybh;
    private String ywlymc;
    private String shzt;
    private String mrmsbh;
    private String mrmsmc;
    private String bdbh;
    private String bdmc;
    private String fwsm;
    private String fwbh;
    private String fwmc;
    private String fwzt;
    private String bqbh;
    private String bqmc;
    private String bbh;
    private String bbsm;
    private String dbbh;
    private String ywlyqc;
    private String sfjqpz;
    private String xgrz;
    private String fzdb;
}
