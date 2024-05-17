$('body').on('click', '#htmlFileButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/getHtmlPath";
    $.ajax({
        type: "GET",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            var path = result;
            $.ajax({
                type: "GET",
                url: path,
                success:function(result) {
                    $("#frameDiv").html(result);
                    console.log("Preview fill"); 
                },
            }); 
        },
    });
});

$('body').on('click', '#openWelcomePage', function() {
    var txt = "Welcome.html";
    var api_path = "/getHtmlPath";
    $.ajax({
        type: "GET",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            window.open(result); 
        },
    });
});