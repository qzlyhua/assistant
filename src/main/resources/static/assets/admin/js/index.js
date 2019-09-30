var IndexPage = function () {
    var init = function () {
        $("#data-table").html("");
        $.ajax({
            url: "/api/xtgl",
            method: 'GET',
            success: function (result) {
                if (result.code == 200) {
                    $.each(result.data, function (idx, obj) {
                        var html = "<tr id=\"tr-" + idx + "\">";
                        html += "<td style=\"text-align:center\">" + obj.xmmc + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.dev + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.test + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.testtjd + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.pro + "</td>";
                        html += "</tr>";
                        $("#data-table").append(html);
                    });

                    $("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
                } else {
                    toastr.clear();
                    toastr.error(result.message);
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
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain + 10);
    toastr.options = {positionClass: "toast-top-center"};
    IndexPage.init();
});