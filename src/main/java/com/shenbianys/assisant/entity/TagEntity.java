package com.shenbianys.assisant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_bq")
public class TagEntity {
    @Id
    private String fwbqid;
    private String fwbqmc;
}
