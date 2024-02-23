function readTextFile(file){
    return function(e){
        // создаем элемент div для вывода текста файла
        const textarea = document.getElementById("textBefore");   
        // переносы строки заменяем на соответствующий текст
        textarea.textContent = e.target.result.replace("\n", "\n");
        textarea.className = "text"; 
    };
}
function printFiles(e) {  
    const file = e.target.files[0];
    const reader = new FileReader(); 
    reader.onload = readTextFile(file);
    reader.readAsText(file);
}
document.getElementById("fileBrowse").addEventListener("change", printFiles);