package com.shenbianys.assistant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Yang Hua
 */
@Data
@Table("fw_sh")
public class ServiceCheckEntity {
    @Id
    private String fwshid;
    private String shsm;
    private String shzt;
    private String fwqdid;
    private Date cjsj;
    private String cjrbh;
    private String cjrxm;
}
