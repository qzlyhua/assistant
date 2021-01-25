var DbComparePage = function() {
	var db1 = $("#db1").val();
	var db2 = $("#db2").val();

	var init = function(){
		var u = "/api/getColumnDiffs/" + db1 + "/" + db2;
		toastr.clear();
		$("#data-table").html("");
		$.ajax({
			url: u,
			method: 'GET',
			success: function(data) {
				if (data.code == 200){
					if (data.result.length == 0){
						toastr.clear();
						$("#loadingDiv").fadeOut(function(){toastr.success("两库一致！")});
					}

					$.each(data.result, function(idx, obj) {
						var id = "tr-" + idx;
						var html = "<tr id=\"" + id + "\">";
						html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
						html += "<td style=\"text-align:center\">" + obj.table + "</td>";
						html += "<td style=\"text-align:center\">" + obj.column + "</td>";
						html += "<td style=\"text-align:center\">" + obj.dshow + "</td>";
						html += "<td style=\"text-align:center\">" + obj.sshow + "</td>";
						html += "</tr>";
						$("#data-table").append(html);
						$("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
					});
				} else {
					toastr.clear();
					data.message && toastr.error(data.message);
				}
			},
			error:function(result) {
			    console.error(result);
				toastr.clear();
				toastr.error(result.status + ":接口调用出错");
			}
		});

		var u2 = "/api/compare/" + db1 + "/" + db2;
		$.ajax({
			url: u2,
			method: 'GET',
			success: function(data) {
				if (data.code == 200){
					$("#sql").text(data.result);
				} else {
					toastr.clear();
					data.message && toastr.error(data.message);
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
	DbComparePage.init();
});