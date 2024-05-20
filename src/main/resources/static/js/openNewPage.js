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

$('body').on('click', '#openOnNewPage', function() {
    var api_path = "/getCurrentSrcPage";
    $.ajax({
        type: "GET",
        url: api_path,
        dataType: "text",
        success:function(result) {
            window.open(result);
            console.log("Current page is set");
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