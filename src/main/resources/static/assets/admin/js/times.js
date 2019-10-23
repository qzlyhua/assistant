var TimesPage = function() {
	var init = function(){
		toastr.clear();
		$("#data-table").html("");

		var times = $.fn.getAjaxJsonData("/api/timesGroupByFwmc");
		$.each(times, function(idx, obj) {
			var id = "tr-" + idx;
			var html = "<tr id=\"" + id + "\">";
			html += "<td style=\"text-align:center\">" + (idx + 1) + "</td>";
			html += "<td style=\"text-align:center\"><a href='/times/" + obj.fwmc + "'>" + obj.fwmc +  "</a></td>";
			html += "<td style=\"text-align:center\">" + obj.fwsm +  "</td>";
			html += "<td style=\"text-align:center\">" + obj.dycs +  "</td>";
			html += "<td style=\"text-align:center\">" + obj.bfb +  "</td>";
			html += "</tr>";
			$("#data-table").append(html);
			$("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
		});

		var tjsjBlockQuote = $.fn.getAjaxJsonData("/api/timeRange");
		$("#tjsjBlockQuote").text(tjsjBlockQuote);
	};

	return {
		init : function(){init();}
	}
}();

jQuery(document).ready(function() {
	var minHeightOfMain = document.documentElement.clientHeight-$("#header").outerHeight()-$("#footer").outerHeight();
	$("#main").css("min-height", minHeightOfMain + 10);
	toastr.options = {positionClass: "toast-top-center"};
	TimesPage.init();
});