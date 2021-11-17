var CsrPage = function () {
    var importWord = function () {
        $("#fielUpload").click();
    };

    var uploadWordFile = function () {
        if ($("#fielUpload")[0].files[0]) {
            $("#tableDiv").hide();
            $("#loadingDiv").show();

            var formData = new FormData();
            formData.append('file', $("#fielUpload")[0].files[0]);

            $.ajax({
                url: "/api/poi/importAndPublish",
                type: 'post',
                async: true,
                processData: false,
                contentType: false,
                data: formData,
                success: function (data) {
                    console.log(data);
                    if (data.code == 200) {
                        toastr.success(data.message);
                        $("#loadingDiv").hide();
                        window.setTimeout(function(){location.reload()}, 2500);
                    } else {
                        toastr.clear();
                        toastr.error(data.message);
                        window.setTimeout(function(){location.reload()}, 3000);
                    }
                },
                error: function (data) {
                    toastr.clear();
                    toastr.error(data.message);
                    window.setTimeout(function(){location.reload()}, 3000);
                }
            });
        }
    };

    return {
        importWord: function () {
            importWord();
        },
        uploadWordFile: function () {
            uploadWordFile();
        }
    }
}();