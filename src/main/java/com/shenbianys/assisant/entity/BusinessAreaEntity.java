package com.shenbianys.assisant.entity;

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
    String ywlybh;
    String ywlymc;
    Date xgsj;
    String xgrbh;
    String xgrxm;
    int ywlyjb;
    String sjywlybh;
    String yylx;
    String ywlyqc;
    String ywlyqc_id;
}
