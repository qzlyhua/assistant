var DbPage = function () {
    var init = function () {
        toastr.clear();
        $("#data-table").html("");
        $.ajax({
            url: "/api/getTableInfos",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var id = "tr-" + idx;
                        var html = "<tr id=\"" + id + "\">";
                        var red = obj.hasUpdate == 1 ? "; color:rgb(242 132 158)" : "";

                        var deva = "<a target=\"_blank\" href=\"" + obj.htmlUrlOfDev + "\">" + obj.versionOfDev + "</a>";
                        var staa = "<a target=\"_blank\" href=\"" + obj.htmlUrlOfStandard + "\">" + obj.versionOfStandard + "</a>";

                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:center" + red + "\">" + obj.sysName + "</td>";
                        html += "<td style=\"text-align:center\" title='查看标准库文档'>" + staa + "</td>";
                        html += "<td style=\"text-align:center\" title='查看开发库文档'>" + deva + "</td>";
                        html += "<td style=\"text-align:center\"><a href=\"" + obj.downloadUrl + "\" class=\"icon fa fa-download\" title='下载标准库文档'></a></td>";
                        html += "<td style=\"text-align:center\"><a href=\"" + obj.compareUrl + "\" class=\"icon fa fa-random\" title='查看数据库差异'></a></td>";
                        html += "</tr>";
                        $("#data-table").append(html);
                        $("#loadingDiv").fadeOut(function () {
                            $("#tableDiv").show()
                        });
                    });
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

    return {
        init: function () {
            init();
        },
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain + 10);
    toastr.options = {positionClass: "toast-top-center"};
    DbPage.init();
});