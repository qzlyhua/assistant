var FwlyPage = function() {
	var init = function(){
		toastr.clear();
		$("#data-table").html("");
		$.ajax({
			url: "/api/lypz",
			method: 'GET',
			success: function(result) {
				if (result.code == "0"){
					$.each(result.data, function(idx, obj) {
						var id = "tr-" + idx;
						var html = "<tr id=\"" + id + "\">";
						html += "<td style=\"text-align:center\">" + obj.cbx + "</td>";
						html += "<td style=\"text-align:center\">" + obj.envmc +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.yhybh +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.yhymc +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.count +  "</td>";
						html += "</tr>";
						$("#data-table").append(html);
						$("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});

						$("#" + id).click(function(){
							let checkBox = $(this).find("input");
							checkBox.prop("checked") ? checkBox.prop("checked", false) : checkBox.prop("checked", true);

							var check = $("input:checkbox:checked").length;
							if (check == 2){
								toastr.clear();
								toastr.success("点【配置比较】查看结果");
								return;
							} else if (check > 2){
								toastr.clear();
								toastr.warning("只能选择两个用户域");
								return;
							}
						});
					});
					toastr.info("选两个用户域进行比较");
				} else {
					toastr.clear();
					result.message && toastr.error(result.message);
				}
			},
			error:function(result) {
			    console.error(result);
				toastr.clear();
				toastr.error(result.status + ":接口调用出错");
			}
		});
	};

	var compare = function () {
		var check = $("input:checkbox:checked").length;
		if (check == 2){
			var url = "/fwly/";
			$("input:checkbox:checked").each(function(){
				url += $(this).attr("id");
				url += "/";
			});
			window.location.href = url.substr(0, url.length - 1);
		} else if (check > 2){
			toastr.clear();
			toastr.error("只能选择两个用户域");
		} else if (check < 2){
			toastr.clear();
			toastr.error("请选择两个用户域");
		}
	};

	return {
		init : function(){init();},
		compare : function () {compare();},
		clear : function () {$("input").prop("checked", false);}
	}
}();

jQuery(document).ready(function() {
	var minHeightOfMain = document.documentElement.clientHeight-$("#header").outerHeight()-$("#footer").outerHeight();
	$("#main").css("min-height", minHeightOfMain + 10);
	toastr.options = {positionClass: "toast-top-center"};
	FwlyPage.init();
});