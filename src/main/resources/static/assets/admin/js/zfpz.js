var ZfpzPage = function() {
	var refresh = function(){
		var current = $("#btnRefresh").text();
		if("隐藏相同" == current) {
			$("#btnRefresh").text("显示全部");
			init("different");
		} else {
			$("#btnRefresh").text("隐藏相同");
			init("all");
		}
	};

	var init = function(type){
		$("#data-table").html("");
		var url = type == "all" ? "/api/zfpz/all": "/api/zfpz/different";
		$.ajax({
			url: url,
			method: 'GET',
			success: function(res) {
				$.each(res, function(idx, obj) {
					var html = "<tr id=\"tr-" + idx + "\">";
					html += "<td style=\"text-align:center\">" + (idx+1) + "</td>";
					html += "<td style=\"text-align:center\">" + obj.path + "</td>";
					html += "<td style=\"text-align:center\">" + obj.ms + "</td>";
					html += "<td id = '" + idx + "-dev" + "' style=\"text-align:center\">" + genBtn('dev', obj.dev, obj.dm, idx+'-dev') + "</td>";
					html += "<td id = '" + idx + "-test" + "' style=\"text-align:center\">" + genBtn('test', obj.test, obj.dm, idx+'-test') + "</td>";
					html += "<td id = '" + idx + "-testtjd" + "' style=\"text-align:center\">" + genBtn('testtjd', obj.testtjd, obj.dm, idx+'-testtjd') + "</td>";
					html += "<td id = '" + idx + "-pro" + "' style=\"text-align:center\">" + genBtn('pro', obj.pro, obj.dm, idx+'-pro') + "</td>";
					html += "</tr>";

					$("#data-table").append(html);
				});

				$(".clsAdd").mouseover(function(){
					$(this).removeClass("fa-close").addClass("fa-plus");
				}).mouseleave(function(){
					$(this).removeClass("fa-plus").addClass("fa-close");
				});
			},
			error:function(result) {
			    console.error(result);
			}
		});
	};

	var genBtn = function(env, type, dm, id){
		if(type == "1") {
			return "<a style='color: rgb(80 210 210)' class=\"icon style1 fa-check\"></a>";
		} else {
			return "<a style='color: rgb(242 132 158)' href=\"javascript:IndexPage.sync('" + dm + "', '" + env + "', '" + id + "')\" class=\"icon style1 clsAdd fa-close\"></a>";
		}
	};

	var sync = function(dm, env, id){
		if ("dev" == env){
			alert("开发环境不支持从其他环境复制服务！");
			return ;
		}
		if (confirm("是否确认在" + env + "环境服务清单增加" + fwmc + "服务？") == true){
			///fwqd/add/{sourceEnv}/{fwmc}/{targetEnv}
			var ok = "<a style='color: rgb(80 210 210)' class=\"icon style1 fa-check\"></a>";
			$.ajax({
			    url: '/fwqd/add/dev/' + dm + '/' + env,
			    type: 'POST',
			    success: function(data) {
			    	console.log(data);
			    	if ("success" == data.result) {
			    		$("#" + id).html(ok);
						alert(data.message);
					} else {
			    		alert(data.message);
						console.error(data.message);
					}
			    }
			});
            return true;
        }
	};
	
	return {
		init : function(type){
	        init(type);
		},
		sync : function(fwmc, env, id){
			sync(fwmc, env, id);
		},
		refresh : function(){
			refresh();
		}
	}
}();

jQuery(document).ready(function() {
	ZfpzPage.init("all");
});