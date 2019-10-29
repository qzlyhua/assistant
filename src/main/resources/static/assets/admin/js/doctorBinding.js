var DoctorBind = function () {
    var init = function () {
        $("#loadingDiv").hide();

        var yhys = $.fn.getAjaxJsonData("/api/getYhys");
        $.each(yhys, function (idx, obj) {
            var opt = "<option value=\"" + obj.jgbh + "\">" + obj.jgmc + "</option>";
            $("#yhy").append(opt)
        });

        $("#yhy").change(function () {
            initDsfxt();
        });
    };

    var initDsfxt = function () {
        $("#dsfxt").val("").html("<option value=\"\">第三方系统</option>");
        var jgbh = $("#yhy").val();
        if (jgbh != "") {
            var dsfxts = $.fn.getAjaxJsonData("/api/getDsfxts/" + jgbh);
            $.each(dsfxts, function (idx, obj) {
                var opt = "<option value=\"" + obj.id + "\">" + obj.xtmc + "</option>";
                $("#dsfxt").append(opt)
            });
        }
    }

    var save = function () {
        var yhy = $("#yhy").val();
        var dsfxt = $("#dsfxt").val();
        var yhms = $("#yhms").val();

        if (yhy && dsfxt && yhms) {
            $("#loadingDiv").show();
            $("#doBindBtn,#form").hide();

            $.ajax({
                type: "POST",
                url: "/api/pro/bind",
                dataType: "json",
                data: $('#form').serialize(),
                cache: false,
                success: function (data) {
                    if ("0" == data.code) {
                        $.each(data.data, function (idx, obj) {
                            var html = "<tr id=\"tr-" + idx + "\">";
                            html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.doctorId + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.doctorUsername + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.systemIdentification + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.username + "</td>";
                            html += "</tr>";
                            $("#data-table").append(html);
                        });

                        TableExport(document.getElementsByTagName("table"), {
                            headers: true,
                            footers: true,
                            formats: ["xlsx"],
                            filename: "医生批量绑定结果",
                            bootstrap: false,
                            exportButtons: true,
                            position: "bottom",
                            ignoreRows: null,
                            ignoreCols: null,
                            trimWhitespace: true,
                            RTL: false,
                            sheetname: "医生批量绑定结果"
                        });

                        $(".xlsx").hide();
                        $("#loadingDiv").hide();
                        $("#tableDiv").show();
                        toastr.success("批量绑定完成");
                    } else {
                        toastr.error(data.message);
                    }
                }
            });
        } else {
            toastr.error("必填项校验错误");
        }
    };

    return {
        save: function () {
            save();
        },
        init: function () {
            init();
        },
        export: function(){
            $(".xlsx").trigger("click");
        }
    }
}();

jQuery(document).ready(function () {
    toastr.options = {positionClass: "toast-top-center"};
    $("#submit-buttom").bind("click", function () {
        DoctorBind.save();
    });
    DoctorBind.init();
});