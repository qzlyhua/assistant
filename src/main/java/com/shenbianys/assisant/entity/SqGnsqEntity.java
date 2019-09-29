package com.shenbianys.assisant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Yang Hua
 */
@Data
@Table("sq_gnsq")
public class SqGnsqEntity {
    @Id
    private Long id;
    private String dm;
    private String mc;
    private String sjdm;
    private Integer lx;
}
