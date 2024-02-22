const fileItemBlock = document.getElementById("fileItemBlock");
// создаем элемент, который представляет отдельный файл на странице
function createFileItem(file){
    const fileItem = document.createElement("div");
    fileItem.className = "fileItem";

    // создаем заголовок для добавляемого файла 
    const fileHeader = document.createElement("h3");
    fileHeader.textContent = file.name;
    fileItem.appendChild(fileHeader);
    return fileItem;
}
function readTextFile(file){
    return function(e){
        const fileItem = createFileItem(file);
        // создаем элемент div для вывода текста файла
        const textarea = document.createElement("textarea");   
        // переносы строки заменяем на соответствующий текст
        textarea.textContent = e.target.result.replace("\n", "\n");
        textarea.className = "text";       
        fileItem.appendChild(textarea); 
        fileItemBlock.appendChild(fileItem);  
    };
}
function printFiles(e) {  
    const file = e.target.files[0];
    const reader = new FileReader(); 
    reader.onload = readTextFile(file);
    reader.readAsText(file);
}
document.getElementById("fileBrowse").addEventListener("change", printFiles);