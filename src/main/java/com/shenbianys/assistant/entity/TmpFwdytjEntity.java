package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Yang Hua
 */
@Data
@Table("tmp_dytj")
public class TmpFwdytjEntity {
    String fwmc;
    String month;
    Long dycs;
}
