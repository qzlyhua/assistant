var CsrVersionPage = function () {
    var init = function () {
        $("#data-table-v").html("");

        $.ajax({
            url: "/api/csrs/v",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var html = "<tr id=\"tr-" + obj.version + "\">";
                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:center\"><a href='../csrs-group-by-version/" + obj.version + "'>" + obj.version + "</a></td>";
                        html += "<td style=\"text-align:center\">" + obj.total + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.lastUpdateTime + "</td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-file-word-o' href='/api/word/" + obj.version + "' target='_blank'></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                        html += "<a class='icon fa fa-file-pdf-o' href='/api/pdf/" + obj.version + "' target='_blank'></a></td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-send-o' href=\"javascript:CsrVersionPage.releaseDocs('" + obj.version + "')\"></a></td>";
                        html += "</tr>";
                        $("#data-table-v").append(html);
                    });

                    $("#loadingDiv").fadeOut(function () {
                        $("#tableDiv").show()
                    });
                } else {
                    toastr.clear();
                    data.message && toastr.error(data.message);
                }
            },
            error: function (result) {
                console.error(result);
                toastr.clear();
                toastr.error("接口调用出错：" + result.status);
            }
        });
    };

    var releaseDocs = function (v) {
        $.ajax({
            url: "/api/md/" + v,
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    toastr.clear();
                    data.message && toastr.success(data.message);
                    window.setTimeout(function () {
                        window.open("https://docs.wiseheartdoctor.cn/#/" + v)
                    }, 3000);
                } else {
                    toastr.clear();
                    data.message && toastr.error(data.message);
                }
            },
            error: function (result) {
                console.error(result);
                toastr.clear();
                toastr.error("接口调用出错：" + result.status);
            }
        });
    };

    return {
        init: function () {
            init();
        },
        releaseDocs: function (v) {
            releaseDocs(v);
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain);
    toastr.options = {positionClass: "toast-top-center"};
    CsrVersionPage.init();
});