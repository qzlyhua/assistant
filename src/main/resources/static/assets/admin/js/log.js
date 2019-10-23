var LogPage = function () {
    var init = function () {
        var myChart = echarts.init(document.getElementById('eCharts'));

        $.ajax({
            url: "/api/timesByYhymcAndFwmc",
            method: 'GET',
            success: function (result) {
                console.log(result);

                if (result.code == "0") {
                    // 指定图表的配置项和数据
                    option = {
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {type : 'shadow'}
                        },
                        legend: {
                            data: result.data.yhymcs
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        xAxis:  {type: 'value'},
                        yAxis: {
                            type: 'category',
                            data: result.data.fwmcs
                        },
                        series: result.data.data
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);

                    $("#loadingDiv").fadeOut(function(){$("#chartDiv").show()});
                } else {
                    toastr.clear();
                    result.message && toastr.error(result.message);
                }
            },
            error: function (result) {
                console.error(result);
                toastr.clear();
                toastr.error(result.status + ":接口调用出错");
            }
        });
    };

    return {
        init: function () {
            init();
        }
    }
}();

jQuery(document).ready(function () {
    var minHeightOfMain = document.documentElement.clientHeight - $("#header").outerHeight() - $("#footer").outerHeight();
    $("#main").css("min-height", minHeightOfMain + 10);
    $("#eCharts").css("width", $("#chartDiv").width() - 260 - 130);
    toastr.options = {positionClass: "toast-top-center"};
    LogPage.init();
});