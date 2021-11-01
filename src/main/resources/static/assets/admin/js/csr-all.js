var CsrAllPage = function () {
    var init = function () {
        $("#data-table").html("");
        var url = "/api/csrs/all";

        if ($("#businessArea").val() != "") {
            url = "/api/csrs/business/" + $("#businessArea").val();
            $("#title").text("传输规范-" + $("#businessArea").val());
        }

        if ($("#version").val()) {
            url = "/api/csrs/version/" + $("#version").val();
            $("#title").text("传输规范-" + $("#version").val());
        }

        $.ajax({
            url: url,
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var html = "<tr id=\"tr-" + obj.id + "\">";
                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:left\"><a href='../csr-view/" + obj.id + "'>" + obj.path + "</a>（" + obj.name + "）"
                            + "<code style='cursor:pointer;' onclick=\"location='../csrs-group-by-version/" + obj.version + "'\">" + obj.version
                            + "</code><code style='cursor:pointer;' onclick=\"location='../csrs-group-by-business-area/" + obj.businessArea + "'\">" + obj.businessArea + "</code></td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-pencil' href='/api/delete/" + obj.id + "' target='_blank'></a></td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-trash' href='/api/delete/" + obj.id + "' target='_blank'></a></td>";
                        html += "</tr>";
                        $("#data-table").append(html);
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
                toastr.error(":接口调用出错：" + result.status);
            }
        });
    };

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
    CsrAllPage.init();
});