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
        var l = 5;
        for (let j = 0; j < l; j++) {
            $("#reqParamsTable").append(genHtml("req", j))
        }

        for (let j = 0; j < l; j++) {
            $("#resParamsTable").append(genHtml("res", j))
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