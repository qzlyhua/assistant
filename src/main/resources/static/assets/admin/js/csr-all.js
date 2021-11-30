var CsrAllPage = function () {
    var deleteOne = function (id) {
        var $a = $("#" + id);
        var now = parseInt(new Date().getTime());
        var last = $a.attr("lastClickTime") ? $a.attr("lastClickTime") : 0;

        if (now - last < 1000) {
            toastr.clear();
            $.ajax({
                url: "/api/csr/del/" + id,
                type: 'DELETE',
                success: function (result) {
                    if (result.code == 200) {
                        toastr.clear();
                        toastr.success("已删除！");
                        $("#tr-" + id).remove();
                    } else {
                        toastr.clear();
                        result.message && toastr.error(result.message);
                    }
                },
                error: function (result) {
                    console.error(result);
                    toastr.clear();
                    toastr.error(result.status + ":接口调用出错");
                }
            });
            return true;
        } else {
            $a.attr("lastClickTime", now);
            toastr.info("请再次点击确认该操作");
        }
    };

    var addToExport = function (id) {
        var btn = $("#btnAddToExport_" + id);
        if (btn.hasClass("fa-plus")) {
            btn.removeClass("fa-plus").addClass("fa-minus");
        } else {
            btn.removeClass("fa-minus").addClass("fa-plus");
        }
        $("#btnExport").text("导出（" + $(".fa-minus").length + "）");
    };

    var exportWord = function () {
        if ($(".fa-minus").length == 0) {
            toastr.error("请添加要导出的接口！");
            return;
        } else {
            var ids = [];
            $(".fa-minus").each(function () {
                ids.push($(this).attr("csrId"));
            });
            var fileName = prompt("请输入文件名", "").trim();
            if (fileName) {
                var url = document.location.protocol + "//" + document.location.host + "/api/exportWord?fileName=" + fileName + "&ids=" + ids.join(",");
                down(url, fileName);
            }
        }
    };

    function down(url, name) {
        toastr.info("正在导出，请稍后...");
        var a = document.createElement("a");
        a.download = name + ".docx";
        a.href = url;
        $("body").append(a);
        a.click();
        $(a).remove();
    };

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
                        html += "<td style=\"text-align:center\"><a id='btnAddToExport_" + obj.id + "' csrId='" + obj.id + "' class='icon fa fa-plus' href='javascript:CsrAllPage.addToExport(" + obj.id + ")'></a></td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-pencil' href='../csr-edit/" + obj.id + "'></a></td>";
                        html += "<td style=\"text-align:center\"><a class='icon fa fa-trash' href='javascript:CsrAllPage.deleteOne(" + obj.id + ")' id='" + obj.id + "' ></a></td>";
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
        },
        deleteOne: function (id) {
            deleteOne(id);
        },
        addToExport: function (id) {
            addToExport(id);
        },
        exportWord: function () {
            exportWord();
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain);
    toastr.options = {positionClass: "toast-top-center"};
    CsrAllPage.init();
});