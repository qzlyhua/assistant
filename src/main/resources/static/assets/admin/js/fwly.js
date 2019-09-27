var FwlyPage = function() {
	var init = function(){
		$("#data-table").html("");
		var url = "/ly";
		$.ajax({
			url: url,
			method: 'GET',
			success: function(res) {
				$.each(res, function(idx, obj) {
					var html = "<tr id=\"tr-" + idx + "\">";
					html += "<td style=\"text-align:center\">" + obj.cbx + "</td>";
					html += "<td style=\"text-align:center\">" + obj.envmc +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.yhybh +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.yhymc +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.count +  "</td>";
					html += "</tr>";
					$("#data-table").append(html);
				});
			},
			error:function(result) {
			    console.error(result);
			}
		});
	};

	var compare = function () {

	}

	return {
		init : function(){
	        init();
		},
		compare : function () {
			compare();
		}
	}
}();

jQuery(document).ready(function() {
	FwlyPage.init();
});