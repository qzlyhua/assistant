var CsrViewPage = function () {
    var init = function () {
        var id = $("#csrId").val();
        $("#btn-edit").attr("href","../csr-edit/" + id);
        $.ajax({
            url: "/api/csr/" + id,
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    var c1 = "<code style=\"float:right\">" +  data.result.apiCsr.businessArea + "</code>";
                    var c2 = "<code style=\"float:right\">" +  data.result.apiCsr.version + "</code>";
                    $("#path").html(data.result.apiCsr.path + "（" + data.result.apiCsr.name + "）" + c1 + c2);
                    $("#description").text(data.result.apiCsr.description);
                    data.result.remarks ? $("#remarks").text(data.result.apiCsr.remarks) : $(".remarks").remove();
                    data.result.req.length > 0 ? $("#reqParamsExample").text(data.result.apiCsr.reqParamsExample) : $(".reqParamsExample").remove();
                    data.result.res.length > 0 ? $("#resParamsExample").text(data.result.apiCsr.resParamsExample) : $(".resParamsExample").remove();

                    data.result.req.length > 0 ? $.each(data.result.req, function (idx, obj) {
                        var html = "<tr>";
                        html += "<td style=\"text-align:center\">" + obj.key + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.type + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.describe + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.required + "</td>";
                        html += "</tr>";
                        $("#table-reqParams").append(html);
                    }) : $("#reqInfo").html("<p>无</p>");

                    data.result.res.length > 0 ? $.each(data.result.res, function (idx, obj) {
                        var html = "<tr>";
                        html += "<td style=\"text-align:center\">" + obj.key + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.type + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.describe + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.required + "</td>";
                        html += "</tr>";
                        $("#table-resParams").append(html);
                    }) : $("#resInfo").html("<p>无</p>");

                    $("#like-version").text(data.result.apiCsr.version);
                    $("#like-business-area").text(data.result.apiCsr.businessArea);

                    $.ajax({
                        url: '/api/csrs/business/' + data.result.apiCsr.businessArea,
                        method: 'GET',
                        success: function (data) {
                            if (data.code == 200) {
                                $.each(data.result, function (idx, obj) {
                                    if (obj.id != id){
                                        var html = "<li><a href='../csr-view/" + obj.id + "'>" + obj.path + "</a>（" + obj.name + "）</li>";
                                        $("#like-business-area-li").append(html);
                                    }
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

                    $.ajax({
                        url: '/api/csrs/version/' + data.result.apiCsr.version,
                        method: 'GET',
                        success: function (data) {
                            if (data.code == 200) {
                                $.each(data.result, function (idx, obj) {
                                    if (obj.id != id){
                                        var html = "<li><a href='../csr-view/" + obj.id + "'>" + obj.path + "</a>（" + obj.name + "）</li>";
                                        $("#like-version-li").append(html);
                                    }
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
    CsrViewPage.init();
});