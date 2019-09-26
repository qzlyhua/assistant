var IndexPage = function() {
	var init = function(){
		$("#data-table").html("");
		var url = "/xtgl";
		$.ajax({
			url: url,
			method: 'GET',
			success: function(res) {
				$.each(res, function(idx, obj) {
					var html = "<tr id=\"tr-" + idx + "\">";
					html += "<td style=\"text-align:center\">" + obj.xmmc + "</td>";
					html += "<td style=\"text-align:center\">" + obj.dev +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.test +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.testtjd +  "</td>";
					html += "<td style=\"text-align:center\">" + obj.pro +  "</td>";
					html += "</tr>";
					$("#data-table").append(html);
				});
			},
			error:function(result) {
			    console.error(result);
			}
		});
	};

	return {
		init : function(){
	        init();
		}
	}
}();

jQuery(document).ready(function() {
	IndexPage.init();
});