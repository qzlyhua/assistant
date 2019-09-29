package com.shenbianys.assisant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Yang Hua
 */
@Data
@Table("xt_dsfzd")
public class ThirdSystemInfoEntity {
    @Id
    private String zdid;
    private String xtmc;
    private String xtbs;
    private String qqms;
    private Integer yhbdms;
    private Boolean sfblxx;
}
