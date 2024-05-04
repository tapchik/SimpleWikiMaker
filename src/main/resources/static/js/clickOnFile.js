$('body').on('click', '#mdDirButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/mdDirButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill"); 
        },
    });
});

$('body').on('click', '#htmlDirButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/htmlDirButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            $("#htmlFiles").html(result);
            console.log("HTML files fill"); 
        },
    });
});