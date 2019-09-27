var FwlyComparePage = function () {
    var refresh = function () {
        var u = $("#envA").val() + "/" + $("#envB").val();
        var current = $("#btnRefresh").text();
        if ("隐藏相同" == current) {
            $("#btnRefresh").text("显示全部");
            init(u + "/different");
        } else {
            $("#btnRefresh").text("隐藏相同");
            init(u + "/all");
        }
    };

    var init = function (u) {
        $("#data-table").html("");
        var url = "/api/lypz/" + u;
        $.ajax({
            url: url,
            method: 'GET',
            success: function (res) {
            	$("#thEnvA").text(res.envA);
				$("#thEnvB").text(res.envB);
                $.each(res.result, function (idx, obj) {
                	var fwmcShow = obj.fwdz ? obj.xtmc + "/" + obj.fwdz + "/" + obj.dsffwmc : obj.xtmc + "/" + obj.dsffwmc;
					var id = "tr-" + idx;
                    var html = "<tr id=\"" + id + "\">";
                    html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                    html += "<td style=\"text-align:center\">" + obj.fwmc + "</td>";
                    html += "<td style=\"text-align:center\">" + fwmcShow + "</td>";
                    html += "<td id = '" + idx + "-a" + "' style=\"text-align:center\">" + genBtn('a', obj.envA, obj.fwmc, idx + '-a') + "</td>";
                    html += "<td id = '" + idx + "-b" + "' style=\"text-align:center\">" + genBtn('b', obj.envB, obj.fwmc, idx + '-b') + "</td>";
                    html += "</tr>";
                    $("#data-table").append(html);

                    $("#" + id).click(function () {
                        let checkBox = $(this).find("input");
                        checkBox.prop("checked") ? checkBox.prop("checked", false) : checkBox.prop("checked", true);
                    });
                });
            },
            error: function (result) {
                console.error(result);
            }
        });
    };

    var genBtn = function (env, type, dm, id) {
        if (type == "1") {
            return "<a style='color: rgb(80 210 210)' class=\"icon style1 fa-check\"></a>";
        } else {
            return "<a style='color: rgb(242 132 158)' href=\"javascript:FwlyComparePage.sync('" + dm + "', '" + env + "', '" + id + "')\" class=\"icon style1 clsAdd fa-close\"></a>";
        }
    };

    var compare = function () {
        var check = $("input:checkbox:checked").length;
        if (check == 2) {
            var url = "/fwly/";
            $("input:checkbox:checked").each(function () {
                url += $(this).attr("id");
                url += "/";
            });
            window.location.href = url.substr(0, url.length - 1);
        } else if (check > 2) {
            alert("仅支持比较两个用户域！");
        } else if (check < 2) {
            alert("请选中两个需要比较的用户域！");
        }
    };

    var clear = function () {
        $("input").prop("checked", false);
    };

    return {
        init: function (url) {
            init(url);
        },
		refresh: function(){
        	refresh();
		},
        compare: function () {
            compare();
        },
        clear: function () {
            clear();
        }
    }
}();

jQuery(document).ready(function () {
    var u = $("#envA").val() + "/" + $("#envB").val();
    FwlyComparePage.init(u + "/all");
});