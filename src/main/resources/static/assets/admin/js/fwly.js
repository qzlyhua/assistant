var FwlyPage = function() {
	var init = function(){
		$("#data-table").html("");
		var url = "/api/ly";
		$.ajax({
			url: url,
			method: 'GET',
			success: function(res) {
				$.each(res, function(idx, obj) {
					var id = "tr-" + idx;
					var html = "<tr id=\"" + id + "\">";
					html += "<td style=\"text-align:center\">" + obj.cbx + "</td>";
					html += "<td style=\"text-align:center\">" + obj.envmc +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.yhybh +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.yhymc +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.count +  "</td>";
					html += "</tr>";
					$("#data-table").append(html);

					$("#" + id).click(function(){
						let checkBox = $(this).find("input");
						checkBox.prop("checked") ? checkBox.prop("checked", false) : checkBox.prop("checked", true);

						var check = $("input:checkbox:checked").length;
						if (check == 2){
							toastr.success("点【配置比较】查看结果");
						} else if (check > 2){
							toastr.warning("只能选择两个用户域");
						}
					});
				});
			},
			error:function(result) {
			    console.error(result);
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
			toastr.error("只能选择两个用户域");
		} else if (check < 2){
			toastr.error("请选择两个用户域");
		}
	};

	var clear = function () {
		$("input").prop("checked", false);
	};

	return {
		init : function(){
	        init();
		},
		compare : function () {
			compare();
		},
		clear : function () {
			clear();
		}
	}
}();

jQuery(document).ready(function() {
	FwlyPage.init();
	toastr.options = {positionClass: "toast-top-center"};
	toastr.info("选两个用户域进行比较");
});