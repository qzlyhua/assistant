package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Yang Hua
 */
@Data
@Table("xt_fwdytj")
public class FwdytjEntity {
    String tjsj;
    String fwmc;
    String yhybh;
    String yhymc;
    Long dycs;
}
