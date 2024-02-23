$('body').on('click', '#convertButton', function() {
    var textBefore = $("#textBefore").val().replace("\n", "\n");
    var api_path = "/button";
    $.ajax({
        type: "POST",
        url: api_path,
        data: {text: textBefore},
        success:function(result) {
            $("#textAfter").val(result);
        },
    });
});