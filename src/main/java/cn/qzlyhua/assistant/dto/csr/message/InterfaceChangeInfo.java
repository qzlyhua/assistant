package cn.qzlyhua.assistant.dto.csr.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口信息
 *
 * @author yanghua
 */
@Data
public class InterfaceChangeInfo {
    private String servicePath;
    private String serviceName;

    private List<Parameter> reqParamsAdded = new ArrayList<>();
    private List<Parameter> reqParamsEdited = new ArrayList<>();
    private List<Parameter> reqParamsDeleted = new ArrayList<>();

    private List<Parameter> resParamsAdded = new ArrayList<>();
    private List<Parameter> resParamsEdited = new ArrayList<>();
    private List<Parameter> resParamsDeleted = new ArrayList<>();
}
