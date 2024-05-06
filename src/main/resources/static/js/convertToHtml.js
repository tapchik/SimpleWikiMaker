$('body').on('click', '#convertAllButton', function() {
    var api_path = "/convertRepoToHtml";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#htmlFiles").html(result);
            console.log("HTML files fill"); 
        },
    });
});