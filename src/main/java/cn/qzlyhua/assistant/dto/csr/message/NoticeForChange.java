package cn.qzlyhua.assistant.dto.csr.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 接口变更通知
 *
 * @author yanghua
 */
@Data
public class NoticeForChange {
    private Date changeTime;
    private String dingTalkNoticeMarkDownText;
    private String changeInfoMarkDownText;

    private boolean isNewVersion;

    private String version;

    /**
     * 新增的接口
     */
    private List<InterfaceChangeInfo> interfaceAdded = new ArrayList<>();

    /**
     * 删除的接口
     */
    private List<InterfaceChangeInfo> interfaceDeleted = new ArrayList<>();

    /**
     * 修改的接口
     */
    private List<InterfaceChangeInfo> interfaceEdited = new ArrayList<>();

    public String getChangeInfoMarkDownText() {
        StringBuffer sb = new StringBuffer("### " + DateUtil.formatDateTime(getChangeTime()) + "\n");
        if (isNewVersion) {
            sb.append("#### 传输规范发布：[" + getVersion() + "](https://docs.wiseheartdoctor.cn/#/" + getVersion() + ")\n");
        } else {
            sb.append("#### 传输规范变更：[" + getVersion() + "](https://docs.wiseheartdoctor.cn/#/" + getVersion() + ")\n");

            if (CollUtil.isNotEmpty(getInterfaceAdded())) {
                sb.append("##### 新增接口：\n");
                for (InterfaceChangeInfo i : getInterfaceAdded()) {
                    sb.append("[" + i.getServicePath() + "（" + i.getServiceName() + "）](https://docs.wiseheartdoctor.cn/#/" + getVersion()
                            + "?id=" + i.getServicePath() + "%EF%BC%88" + URLUtil.encode(i.getServiceName()) + "%EF%BC%89" + ")\n");
                }
            }

            if (CollUtil.isNotEmpty(getInterfaceDeleted())) {
                sb.append("##### 删除接口：\n");
                for (InterfaceChangeInfo i : getInterfaceAdded()) {
                    sb.append(i.getServicePath() + "（" + i.getServiceName() + "）\n");
                }
            }

            if (CollUtil.isNotEmpty(getInterfaceEdited())) {
                sb.append("##### 修改接口：\n");
                for (InterfaceChangeInfo i : getInterfaceEdited()) {
                    sb.append("###### [" + i.getServicePath() + "（" + i.getServiceName() + "）](https://docs.wiseheartdoctor.cn/#/" + getVersion()
                            + "?id=" + i.getServicePath() + "%EF%BC%88" + URLUtil.encode(i.getServiceName()) + "%EF%BC%89" + ")\n");

                    appendParametersTable(sb, "新增入参", i.getReqParamsAdded());
                    appendParametersTable(sb, "删除入参", i.getReqParamsDeleted());
                    appendParametersTable(sb, "修改入参", i.getReqParamsEdited());
                    appendParametersTable(sb, "新增出参", i.getResParamsAdded());
                    appendParametersTable(sb, "删除出参", i.getResParamsDeleted());
                    appendParametersTable(sb, "修改出参", i.getResParamsEdited());
                }
            }
        }
        sb.append("----\n");
        return sb.toString();
    }

    private static void appendParametersTable(StringBuffer sb, String title, List<Parameter> parameters) {
        if (CollUtil.isNotEmpty(parameters)) {
            sb.append("###### " + title + "：\n\n");
            sb.append("| 属性名 | 类型 | 描述 | 必填 |\n");
            sb.append("| :----- | :----: | :----- | :----: |\n");
            for (Parameter p : parameters) {
                sb.append("| " + p.getKey() + " | " + p.getType() + " | " + p.getDes() + " | " + p.getIsRequired() + " | \n");
            }
        }
    }

    public String getDingTalkNoticeMarkDownText() {
        //https://docs.wiseheartdoctor.cn/#/changelog?id=_2021-11-26-165032
        StringBuffer sb = new StringBuffer("#### 传输规范发布通知\n");
        if (isNewVersion) {
            sb.append("\uD83D\uDCE3 传输规范发布：[" + getVersion() + "](https://docs.wiseheartdoctor.cn/#/" + URLUtil.encode(getVersion()) + ")\n");
        } else {
            sb.append("\uD83D\uDCE3 传输规范变更：[" + getVersion() + "](https://docs.wiseheartdoctor.cn/#/changelog?id=_" + DateUtil.format(getChangeTime(), "yyyy-MM-dd-hhmmss") + ")\n");

        }
        return sb.toString();
    }
}
