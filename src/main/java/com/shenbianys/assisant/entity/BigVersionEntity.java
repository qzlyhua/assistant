package com.shenbianys.assisant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_bbgh")
public class BigVersionEntity {
    @Id
    private String id;
    private Date cjsj;
    private String cjrbh;
    private String cjrxm;
    private String bbmc;
}
