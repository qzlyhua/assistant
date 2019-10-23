var TimesByFwmcPage = function() {
	var init = function(){
		toastr.clear();
		$("#data-table").html("");
		$.ajax({
			url: "/api/timesOfFwmcGroupByYhymc/" + $("#fwmc").val(),
			method: 'GET',
			success: function(result) {
				if (result.code == "0"){
					$.each(result.data, function(idx, obj) {
						var id = "tr-" + idx;
						var html = "<tr id=\"" + id + "\">";
						html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
						html += "<td style=\"text-align:center\">" + obj.yhymc +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.dycs +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.bfb +  "</td>";
						html += "</tr>";
						$("#data-table").append(html);
						$("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
					});
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

	return {
		init : function(){init();}
	}
}();

jQuery(document).ready(function() {
	var minHeightOfMain = document.documentElement.clientHeight-$("#header").outerHeight()-$("#footer").outerHeight();
	$("#main").css("min-height", minHeightOfMain + 10);
	toastr.options = {positionClass: "toast-top-center"};
	$("#fwdyqkTitle").attr("title", $("#fwmc").val());
	TimesByFwmcPage.init();
});