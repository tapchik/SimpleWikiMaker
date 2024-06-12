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
    $.ajax({
        dataType: "text",
        success:function() {
            window.open("/p/Welcome");
            console.log("Current page is set");
        },
    });
});

$('body').on('click', '.relativeLink', function(e) {
    var href = $(this).attr('href');
    document.location = "/p" + href;
    return false;
});