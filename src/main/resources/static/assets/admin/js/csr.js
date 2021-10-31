var CsrPage = function () {
    var init = function () {
        $("#data-table-b").html("");
        $("#data-table-v").html("");

        var groupByVersion = function () {
            return $.ajax({
                url: "/api/csrs/v",
                method: 'GET'
            })
        };

        var groupByBusinessArea = function () {
            return $.ajax({
                url: "/api/csrs/b",
                method: 'GET',
            })
        };

        $.when(groupByVersion(), groupByBusinessArea())
            .then(
                function (resOfVersion, resOfBusinessArea) {
                    if (resOfVersion[0].code == 200) {
                        $.each(resOfVersion[0].result, function (idx, obj) {
                            var html = "<tr id=\"tr-" + obj.version + "\">";
                            html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.version + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.total + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.lastUpdateTime + "</td>";
                            html += "<td style=\"text-align:center\"><a class='icon fa fa-file-word-o' href='/api/poi/" + obj.version + "' target='_blank'></a></td>";
                            html += "</tr>";
                            $("#data-table-v").append(html);
                        });
                    } else {
                        toastr.clear();
                        resOfVersion[0].message && toastr.error(resOfVersion[0].message);
                    }
                    ;

                    if (resOfBusinessArea[0].code == 200) {
                        $.each(resOfBusinessArea[0].result, function (idx, obj) {
                            var html = "<tr id=\"tr-" + obj.businessArea + "\">";
                            html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.businessArea + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.total + "</td>";
                            html += "<td style=\"text-align:center\">" + obj.lastUpdateTime + "</td>";
                            html += "<td style=\"text-align:center\"><a class='icon fa fa-file-word-o' href='/api/poi/" + obj.businessArea + "' target='_blank'></a></td>";
                            html += "</tr>";
                            $("#data-table-b").append(html);
                        });
                    } else {
                        toastr.clear();
                        resOfBusinessArea[0].message && toastr.error(resOfBusinessArea[0].message);
                    }
                    ;

                    $("#loadingDiv").fadeOut(function () {
                        $("#tableDiv").show()
                    });
                },
                function (error) {
                    console.error(error);
                    toastr.clear();
                    toastr.error(":接口调用出错：" + error.status);
                }
            );
    };

    //触发file的input
    var importWord = function () {
        $("#fielUpload").click();
    };

    var uploadWordFile = function () {
        if ($("#fielUpload")[0].files[0]) {
            var formData = new FormData();
            // 获取上传文件的数据
            formData.append('file', $("#fielUpload")[0].files[0]);

            $.ajax({
                url: "/api/poi/import",
                type: 'post',
                async: false,
                processData: false,
                contentType: false,
                data: formData,
                success: function (data) {
                    console.log(data);
                    if (data.code == 200) {
                        toastr.success(data.message);
                        window.setTimeout(init(), 2000);
                    } else {
                        toastr.clear();
                        toastr.error(data.message);
                    }
                },
                error: function (data) {
                    toastr.clear();
                    toastr.error(data.message);
                }
            });

            $("#fielUpload").val("");
        }
    };

    var add = function () {
        alert(22);
    };

    return {
        init: function () {
            init();
        },
        importWord: function () {
            importWord();
        },
        uploadWordFile: function () {
            uploadWordFile();
        },
        add: function () {
            add();
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain);
    toastr.options = {positionClass: "toast-top-center"};
    CsrPage.init();
});