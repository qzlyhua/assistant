var IndexPage = function () {
    var init = function () {
        $("#data-table").html("");
        $.ajax({
            url: "/api/origins",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    $.each(data.result, function (idx, obj) {
                        var html = "<tr id=\"tr-" + obj.id + "\">";
                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.originCode + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.originName + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.authCode + "</td>";
                        html += "<td style=\"text-align:center\"><a href='" + obj.address + "' target='_blank'>" + obj.address + "</a></td>";
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
    IndexPage.init();
});