var FwlyPage = function () {
    var init = function () {
        toastr.clear();
        $("#data-table").html("");
        $.ajax({
            url: "/api/routeConfigInfos",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var id = "tr-" + idx;
                        var html = "<tr id=\"" + id + "\">";
                        html += "<td style=\"text-align:center\">" + obj.checkBoxHtml + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.envType + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.originCode + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.originName + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.count + "</td>";
                        html += "</tr>";
                        $("#data-table").append(html);
                        $("#loadingDiv").fadeOut(function () {
                            $("#tableDiv").show()
                        });

                        $("#" + id).click(function () {
                            let checkBox = $(this).find("input");
                            checkBox.prop("checked") ? checkBox.prop("checked", false) : checkBox.prop("checked", true);

                            var check = $("input:checkbox:checked").length;
                            if (check == 2) {
                                toastr.clear();
                                toastr.success("点【配置比较】查看结果");

                            } else if (check > 2) {
                                toastr.clear();
                                toastr.warning("只能选择两个用户域");

                            }
                        });
                    });
                    toastr.info("选两个用户域进行比较");
                } else {
                    toastr.clear();
                    data.message && toastr.error(data.message);
                }
            },
            error: function (result) {
                console.error(result);
                toastr.clear();
                toastr.error(result.status + ":接口调用出错");
            }
        });
    };

    var compare = function () {
        var check = $("input:checkbox:checked").length;
        if (check == 2) {
            var url = "/route/";
            $("input:checkbox:checked").each(function () {
                url += $(this).attr("id");
                url += "/";
            });
            window.location.href = url.substr(0, url.length - 1);
        } else if (check > 2) {
            toastr.clear();
            toastr.error("只能选择两个用户域");
        } else if (check < 2) {
            toastr.clear();
            toastr.error("请选择两个用户域");
        }
    };

    return {
        init: function () {
            init();
        },
        compare: function () {
            compare();
        },
        clear: function () {
            $("input").prop("checked", false);
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain + 10);
    toastr.options = {positionClass: "toast-top-center"};
    FwlyPage.init();
});