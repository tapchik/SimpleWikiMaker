$('body').on('click', '#htmlFileButton', function() {
    var txt = $(this).val().toString();
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