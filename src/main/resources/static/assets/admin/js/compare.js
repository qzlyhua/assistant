var Page = function() {
	var urlAll = $("#urlAll").val();
	var urlDifferent = $("#urlDifferent").val();
	var tableTdCodes = $("#tableTdCodes").val();
	var titles = $("#titles").val();
	var syncUrl  = $("#syncUrl").val();

	// 切换显示全部、隐藏相同
	var refresh = function(){
		$("html,body").animate({scrollTop: 0},100);
		$("#tableDiv").hide();
		$("#loadingDiv").show();
		$("#data-table").html("");
		$("#btnRefresh").text("隐藏相同" == $("#btnRefresh").text() ? "显示全部" : "隐藏相同");
		init();
	};

	var init = function(){
		toastr.clear();
		$.ajax({
			url:  "隐藏相同" == $("#btnRefresh").text() ? urlAll : urlDifferent,
			method: 'GET',
			success: function(res) {
				$.each(res, function(idx, obj) {
					$("#data-table").append("<tr id=\"tr-" + idx + "\">" + genTr(idx + 1, obj) + "</tr>");
					titles &&  $(titles.split(",")).each(function(i, v){processTitle(v,idx + 1, obj);});
				});

				$(".claSync").mouseover(function(){
					$(this).removeClass("fa-close").addClass("fa-plus");
				}).mouseleave(function(){
					$(this).removeClass("fa-plus").addClass("fa-close");
				});

				$("#loadingDiv").fadeOut(function(){$("#tableDiv").show()});
				res.length == 0 && toastr.info("暂无数据");
			},
			error:function(result) {
				toastr.error("数据获取失败");
			    console.error(result);
			}
		});
	};

	var genTr = function(rowNumber, data){
		var tr = "";
		$(tableTdCodes.split(",")).each(function(i, o){
			if ("idx" == o) {
				tr += "<td style=\"text-align:center\">" + rowNumber + "</td>";
			} else if("dev" == o || "test" == o || "testtjd" == o || "pro" == o){
				tr += "<td style=\"text-align:center\">" + genBtn(o, data[o], data.key)  + "</td>";
			} else {
				tr += "<td id=\"tr-" + rowNumber + "-" + o + "\" style=\"text-align:center\">" + data[o] + "</td>";
			}
		});
		return tr;
	};

	var processTitle = function(rule, rowNumber, data){
		var t = rule.split(":")[0];
		var v = rule.split(":")[1];
		var id = "tr-" + rowNumber + "-" + t;
		$("#" + id).attr("title", data[v]);
	};

	var genBtn = function(env, type, key){
		if(type == "1") {
			return "<span style='color: rgb(80 210 210)' class=\"icon fa-check\"></span>";
		} else {
			var aid = (env + "_" + key).replace(/\./g, "_");
			return "<a style='color: rgb(242 132 158)' id = \"" + aid + "\" href=\"javascript:Page.sync('" + key + "', '" + env + "', '" + aid + "')\" class=\"icon fa claSync fa-close\"></a>";
		}
	};

	var sync = function(key, env, id){
		if (!syncUrl){
			toastr.clear();
			toastr.error($("#pageH2").text() + "不支持该操作");
			return;
		}

		if ("dev" == env){
			toastr.error("开发环境不支持该操作");
			return;
		}

		var $a = $("#" + id);
		var now = parseInt(new Date().getTime()/1000);
		var last = $a.attr("lastClickTime") ? $a.attr("lastClickTime") : 0;

		if(now - last < 3){
			$a.removeClass("fa-plus claSync").addClass("fa-circle-o-notch fa-spin");
			$.ajax({
				url: syncUrl + '/' + env + '/' + key,
				type: 'GET',
				success: function(data) {
					if ("success" == data.result) {
						$a.parent().html("<span style='color: rgb(80 210 210)' class=\"icon fa-check\"></span>");
						toastr.clear();
						toastr.success("操作成功");
					} else {
						$a.removeClass("fa-circle-o-notch fa-spin").addClass("claSync fa-close");
						toastr.clear();
						toastr.error(data.message);
					}
				},
				error:function(res){
					console.error(res);
					$a.removeClass("fa-circle-o-notch fa-spin").addClass("claSync fa-close");
					toastr.clear();
					toastr.error(res.status + ":接口调用出错");
				}
			});
			return true;
		} else {
			$a.attr("lastClickTime", now);
			toastr.info("请再次点击确认该操作");
			return;
		}
	};
	
	return {
		init : function(){init();},
		sync : function(fwmc, env, id){sync(fwmc, env, id);},
		refresh : function(){refresh();}
	}
}();

jQuery(document).ready(function() {
	var minHeightOfMain = document.documentElement.clientHeight-$("#header").outerHeight()-$("#footer").outerHeight();
	$("#main").css("min-height", minHeightOfMain + 10);
	toastr.options = {positionClass: "toast-top-center"};
	Page.init();
});