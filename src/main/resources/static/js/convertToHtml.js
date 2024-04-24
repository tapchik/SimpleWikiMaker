$('body').on('click', '#convertButton', function() {
    var textBefore = $("#textBefore").val().replace("\n", "\n");
    var api_path = "/convertButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        data: {text: textBefore},
        success:function(result) {
            $("#textAfter").val(result);
        },
    });
});

$('body').on('click', '#convertAllButton', function() {
    var api_path = "/convertRepoToHtml";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#htmlFilesFolder").html(result);
        },
    });
});