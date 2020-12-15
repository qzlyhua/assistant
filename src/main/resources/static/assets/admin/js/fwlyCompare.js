var FwlyComparePage = function () {
    var envA = $("#envA").val();
    var envB = $("#envB").val();

    // 切换显示全部、隐藏相同
    var changeAndRefresh = function () {
        $("html,body").animate({scrollTop: 0}, 100);
        $("#tableDiv").hide();
        $("#loadingDiv").show();
        $("#data-table").html("");
        $("#btnRefresh").text("隐藏相同" == $("#btnRefresh").text() ? "显示全部" : "隐藏相同");
        init();
    };

    var refresh = function () {
        $("html,body").animate({scrollTop: 0}, 100);
        $("#tableDiv").hide();
        $("#loadingDiv").show();
        $("#data-table").html("");
        init();
    };

    var init = function () {
        $("#data-table").html("");
        var u = "/api/routeCompare/" + envA + "/" + envB + "/";
        $.ajax({
            url: "隐藏相同" == $("#btnRefresh").text() ? u + "all" : u + "different",
            method: 'GET',
            success: function (data) {
                if (data.code == 200) {
                    var res = data.result;
                    $("#thEnvA").attr("title", res.envA);
                    $("#thEnvB").attr("title", res.envB);
                    $.each(res.result, function (idx, obj) {
                        var fwmcShow = obj.application + obj.service;
                        var id = "tr-" + idx;
                        var html = "<tr id=\"" + id + "\">";
                        html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
                        html += "<td style=\"text-align:center\">" + obj.route + "</td>";
                        html += "<td style=\"text-align:center\">" + fwmcShow + "</td>";
                        html += "<td id = 'td-" + idx + "-a" + "' style=\"text-align:center\">" + genBtn(envA, obj.envA, obj.route, idx + '-a') + "</td>";
                        html += "<td id = 'td-" + idx + "-b" + "' style=\"text-align:center\">" + genBtn(envB, obj.envB, obj.route, idx + '-b') + "</td>";
                        html += "</tr>";
                        $("#data-table").append(html);
                    });

                    $(".claSync").mouseover(function(){
                        $(this).removeClass("fa-close").addClass("fa-plus");
                    }).mouseleave(function(){
                        $(this).removeClass("fa-plus").addClass("fa-close");
                    });

                    $("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
                    res.length == 0 && toastr.info("暂无数据");
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

    var genBtn = function (env, type, fwmc, id) {
        if (type == "1") {
            return "<span style='color: rgb(80 210 210)' class=\"icon fa-check\"></span>";
        } else {
            var from = envA == env ? envB : envA;
            var to = envA == env ? envA : envB;

            var btn = "<a style='color: rgb(242 132 158)' id='" + id + "' class=\"icon claSync fa fa-close\"" +
                "href=\"javascript:FwlyComparePage.sync('" + from + "','" + to + "','" + fwmc + "','" + id + "')\"></a>";
            return btn;
        }
    };

    var sync = function (from, to, fwmc, id) {
        var $a = $("#" + id);
        var now = parseInt(new Date().getTime()/1000);
        var last = $a.attr("lastClickTime") ? $a.attr("lastClickTime") : 0;

        if(now - last < 3){
            toastr.clear();
            $a.removeClass("fa-plus claSync").addClass("fa-circle-o-notch fa-spin");
            $.ajax({
                url: "/api/route/sync/" + from + "/" + to + "?route=" + encodeURIComponent(fwmc) ,
                type: 'GET',
                success: function(result) {
                    if (result.code == 200) {
                        $a.parent().html("<span style='color: rgb(80 210 210)' class=\"icon fa-check\"></span>");
                        toastr.clear();
                        toastr.success("同步完成");
                    } else {
                        $a.removeClass("fa-circle-o-notch fa-spin").addClass("claSync fa-close");
                        toastr.clear();
                        result.message && toastr.error(result.message);
                    }
                },
                error:function(result){
                    console.error(result);
                    $a.removeClass("fa-circle-o-notch fa-spin").addClass("claSync fa-close");
                    toastr.clear();
                    toastr.error(result.status + ":接口调用出错");
                }
            });
            return true;
        } else {
            $a.attr("lastClickTime", now);
            toastr.info("请再次点击确认该操作");
            return;
        }
    };

    return {
        init: function () {init();},
        changeAndRefresh: function () {changeAndRefresh();},
        refresh: function () {refresh();},
        sync: function (from, to, fwmc, id) {sync(from, to, fwmc, id);}
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain + 10);
    toastr.options = {positionClass: "toast-top-center"};
    FwlyComparePage.init();
});