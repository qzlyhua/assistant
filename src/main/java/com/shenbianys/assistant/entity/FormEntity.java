package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("bd_bd")
public class FormEntity {
    @Id
    private String bdbh;
    private Date xgsj;
    private String xgrbh;
    private String xgrxm;
    private String ywlybh;
    private String ywlymc;
    private String bdmc;
    private String bddysm;
    private String yylx;
    private String ywlyqc;
    private String ywlyqc_id;
}
