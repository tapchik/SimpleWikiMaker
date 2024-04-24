$('body').on('click', '#openOnNewPage', function() {
    var copyHTML = $('#htmlFilesFolder').html();
    var newWindow = window.open('newPage');
    newWindow.document.body.innerHTML = copyHTML;
});