var IndexPage_User = function() {
	var init = function(){
		$.ajax({
			url: "/user/htdz",
			method: 'POST',
			success: function(res) {
				if(res.code == "0"){
					$.each(res.result, function(idx, obj) {
						var xtjgbh = '', xtjgmc = '', jgxx = '';

						if(obj.xtjgxx && obj.xtjgxx.xtjgbh && obj.xtjgxx.xtjgmc){
							xtjgbh = obj.xtjgxx.xtjgbh;
							xtjgmc = obj.xtjgxx.xtjgmc;
							jgxx = xtjgmc + '（' + xtjgbh + '）';
						}

						var html = "<tr id=\"tr-" + obj.jbxx.id + "\">";
						html += "<td style=\"text-align:center\">" + obj.jbxx.appid + "</td>";
						html += "<td style=\"text-align:center\">" + obj.jbxx.appmc +  "</td>";
						html += "<td style=\"text-align:center\">" + obj.htdzxx.htdz + "</td>";
						html += "<td style=\"text-align:center\">" + jgxx + "</td>";
						html += "</tr>";
						$("#data-table").append(html);
					});
				}
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
	IndexPage_User.init();
});