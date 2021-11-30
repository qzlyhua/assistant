var CsrBusinessAreaPage = function () {
    var init = function () {
        $("#data-table-b").html("");

        $.ajax({
            url: "/api/csrs/b",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var html = "<tr id=\"tr-" + obj.businessArea + "\">";
                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:center\"><a href='../csrs-group-by-business-area/" + obj.businessArea + "'>" + obj.businessArea + "</a></td>";
                        html += "<td style=\"text-align:center\">" + obj.total + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.lastUpdateTime + "</td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-file-word-o' href='/api/word/" + obj.businessArea + "' target='_blank'></a>&nbsp;&nbsp;&nbsp;&nbsp;";
                        html += "<a class='icon fa fa-file-pdf-o' href='/api/pdf/" + obj.businessArea + "' target='_blank'></a></td>";
                        html += "</tr>";
                        $("#data-table-b").append(html);
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
    CsrBusinessAreaPage.init();
});