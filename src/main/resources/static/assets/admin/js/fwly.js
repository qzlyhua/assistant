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
			alert("仅支持比较两个用户域！");
		} else if (check < 2){
			alert("请选中两个需要比较的用户域！");
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
});