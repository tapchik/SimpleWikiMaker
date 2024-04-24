$('body').on('click', '#mdDirButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/mdDirButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            $("#mdFilesFolder").html(result);
            console.log("File repository(folder) fill"); 
        },
    });
});

$('body').on('click', '#mdFileButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/mdFileButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            $("#textBefore").val(result);
            console.log("Text is show"); 
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
            $("#htmlFilesFolder").html(result);
            console.log("File repository(folder) fill"); 
        },
    });
});

$('body').on('click', '#htmlFileButton', function() {
    var txt = $(this).val().toString();
    var api_path = "/htmlFileButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        dataType: "text",
        data: {name: txt.toString()},
        success:function(result) {
            $("#textAfter").val(result);
            console.log("Text is show"); 
        },
    });
});