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
    var api_path = "/defaultPageFrame";
    $.ajax({
        type: "GET",
        url: api_path,
        success:function(result) {
            $("#frameDiv").html(result);
            console.log("Preview fill"); 
        }
    })
});