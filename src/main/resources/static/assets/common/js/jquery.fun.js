$.fn.getAjaxJsonData = function (url) {
    var data = null;
    $.ajax({
        method:"GET",
        url:url,
        cache:false,
        async:false,
        success: function(result) {
            if (result.code == "0"){
                data = result.data;
            } else {
                toastr.clear();
                result.message && toastr.error(result.message);
            }
        },
        error: function (result) {
            console.error(result);
        }
    });
    return data;
};