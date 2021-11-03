var CsrAddPage = function () {
    var genHtml = function (t, i){
        var html = "<div class=\"2u\"></div>" +
            "       <div class=\"3u\">" +
            "           <input type=\"text\" value=\"\" name=\"" + t + "-name\">" +
            "       </div>" +
            "       <div class=\"2u\">" +
            "           <div class=\"select-wrapper\">" +
            "               <select name=\"" + t + "-type\" style=\"text-align:center;\">" +
            "                   <option value=\"字符串\">字符串</option>" +
            "                   <option value=\"数字\">数字</option>" +
            "                   <option value=\"布尔值\">布尔值</option>" +
            "                   <option value=\"JSON数组\">JSON数组</option>" +
            "                   <option value=\"JSON对象\">JSON对象</option>" +
            "                   <option value=\"字符串数组\">字符串数组</option>" +
            "                   <option value=\"任意属性\">任意属性</option>" +
            "               </select>" +
            "           </div>" +
            "       </div>" +
            "       <div class=\"4u\">" +
            "           <input type=\"text\" value=\"\" name=\"" + t + "-des\">" +
            "       </div>" +
            "       <div class=\"1u\">" +
            "           <input type=\"checkbox\" name=\"" + t + "-requied-" + i + "\" id=\"" + t + "-requied-" + i + "\">" +
            "           <label for=\"" + t + "-requied-" + i + "\" style=\"margin: 0 25%;\"></label>" +
            "       </div>";
        return html;
    }

    var init = function () {
        var csrId = $("#csrId").val();
        if (csrId){
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
            var l = 5;
            for (let j = 0; j < l; j++) {
                $("#reqParamsTable").append(genHtml("req", j))
            }

            for (let j = 0; j < l; j++) {
                $("#resParamsTable").append(genHtml("res", j))
            }
        }
    }

    return {
        init: function (){
            init();
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain);
    toastr.options = {positionClass: "toast-top-center"};
    CsrAddPage.init();
});