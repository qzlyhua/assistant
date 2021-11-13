var CsrAddPage = function () {
    var genHtml = function (t, i, pKey, pType, pDescribe, pRequired) {
        var isChecked = pRequired === "Y";
        var html = "<div class=\"2u\"></div>" +
            "       <div class=\"3u\">" +
            "           <input id='" + t + "-pKey-" + i + "' type=\"text\" value='" + pKey + "' name=\"" + t + "-name\">" +
            "       </div>" +
            "       <div class=\"2u\">" +
            "           <div class=\"select-wrapper\">" +
            "               <select id='" + t + "-type-" + i + "' name=\"" + t + "-type\" value='" + pType + "' style=\"text-align:center;\">" +
            "                   <option value=\"字符串\">字符串</option>" +
            "                   <option value=\"字符串数组\">字符串数组</option>" +
            "                   <option value=\"数字\">数字</option>" +
            "                   <option value=\"布尔值\">布尔值</option>" +
            "                   <option value=\"JSON对象\">JSON对象</option>" +
            "                   <option value=\"JSON数组\">JSON数组</option>" +
            "                   <option value=\"文件\">文件</option>" +
            "                   <option selected='true' value=\"-\">-</option>" +
            "               </select>" +
            "           </div>" +
            "       </div>" +
            "       <div class=\"4u\">" +
            "           <input type=\"text\" value='" + pDescribe + "' name=\"" + t + "-des\">" +
            "       </div>" +
            "       <div class=\"1u\">" +
            "           <input type=\"checkbox\" " + (isChecked ? " checked " : "") + "name=\"" + t + "-requied-" + i + "\" id=\"" + t + "-requied-" + i + "\">" +
            "           <label for=\"" + t + "-requied-" + i + "\" style=\"margin: 0 25%;\"></label>" +
            "       </div>";
        return html;
    }

    var init = function () {
        var csrId = $("#csrId").val();
        var aci = 0;
        if (csrId) {
            $.ajax({
                url: "/api/csr/" + csrId,
                method: 'GET',
                success: function (data) {
                    if (data.code == 200) {
                        $("#ipt-path").val(data.result.apiCsr.path);
                        $("#ipt-name").val(data.result.apiCsr.name);
                        $("#ipt-description").val(data.result.apiCsr.description);
                        $("#ipt-version").val(data.result.apiCsr.version);
                        $("#ipt-businessAreaName").val(data.result.apiCsr.businessArea);
                        $("#ipt-remarks").val(data.result.apiCsr.remarks);
                        $("#ipt-reqExample").val(data.result.apiCsr.reqParamsExample).focus().blur();
                        $("#ipt-resExample").val(data.result.apiCsr.resParamsExample).focus().blur();

                        if (data.result.req.length > 0) {
                            $.each(data.result.req, function (idx, obj) {
                                $("#reqParamsTable").append(genHtml("req", idx + 1, obj.key, obj.type, obj.describe, obj.required));
                                $("#req-type-" + (idx + 1)).val(obj.type);
                                autoComplate($("#req-pKey-" + (idx + 1)), ++aci);
                            });
                            $("#reqParamsTable").append(genHtml("req", data.result.req.length + 1, '', '', '', 'N'));
                            autoComplate($("#req-pKey-" + (data.result.req.length + 1)), ++aci);
                        } else {
                            for (let j = 1; j < 5; j++) {
                                $("#reqParamsTable").append(genHtml("req", j, '', '', '', 'N'));
                                autoComplate($("#req-pKey-" + j), ++aci);
                            }
                        }

                        if (data.result.res.length > 0) {
                            $.each(data.result.res, function (idx, obj) {
                                $("#resParamsTable").append(genHtml("res", idx + 1, obj.key, obj.type, obj.describe, obj.required));
                                $("#res-type-" + (idx + 1)).val(obj.type);
                                autoComplate($("#res-pKey-" + (idx + 1)), ++aci);
                            });
                            $("#resParamsTable").append(genHtml("res", data.result.res.length + 1, '', '', '', 'N'));
                            autoComplate($("#res-pKey-" + (data.result.res.length + 1)), ++aci);
                        } else {
                            for (let j = 1; j < 5; j++) {
                                $("#resParamsTable").append(genHtml("res", j, '', '', '', 'N'));
                                autoComplate($("#req-pKey-" + j), ++aci);
                            }
                        }
                    } else {
                        toastr.clear();
                        data.message && toastr.error(data.message);
                    }
                },
                error: function (result) {
                    console.error(result);
                    toastr.clear();
                    toastr.error(":接口调用出错：" + result.status);
                }
            });
        } else {
            for (let j = 1; j < 5; j++) {
                $("#reqParamsTable").append(genHtml("req", j, '', '', '', ''))
            }

            for (let j = 1; j < 5; j++) {
                $("#resParamsTable").append(genHtml("res", j, '', '', '', ''))
            }
        }

        toastr.error("修改功能暂未开发！");
    }

    var autoComplate = function (obj, i){
        $(obj).autocompleter({
            highlightMatches: true,
            source: "/api/paramKeysRec",
            template: '{{ label }} <span>（{{ describe }} | {{ type }} | {{ required }}）</span>',
            hint: true,
            empty: false,
            limit: 10,
            callback: function (value, index, selected) {
                if (selected) {
                    //alert(selected.label);
                }
            }
        });

        $("#autocompleter-" + i).css("top", 60 + top + "px");
    }

    return {
        init: function () {
            init();
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain);
    toastr.options = {positionClass: "toast-top-center"};
    CsrAddPage.init();
});