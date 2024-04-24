$('body').on('click', '#pickFilesButton', async (evt) => {
    const out = {};
    const dirHandle = await showDirectoryPicker();
    const fileList = [];
    await handleDirectoryEntry(dirHandle.name.toString(), dirHandle, out, fileList);
    var apiPath = "/setCurrentFolder";
    $.ajax({
        type: "POST",
        url: apiPath,
        data: {dirHandle: dirHandle.name.toString()},
        success:function(result) {
            var apiPath = "/listOfMdFiles";
            $.ajax({
                type: "POST",
                url: apiPath,
                data: JSON.stringify(fileList),
                dataType: "text",
                contentType:'application/json',
                success:function(result) {
                    $("#mdFilesFolder").html(result);
                    console.log("File repository(folder) fill");
                },
            });
        },
    });
});

async function handleDirectoryEntry(filePath, dirHandle, out, fileList) {
    for await (const entry of dirHandle.values()) {
        if (entry.kind === "file") {
            const file = await entry.getFile();
            if (file.name.split(".").pop() === "md") {
                const fileContent = await file.text();
                out[file.name] = {};
                const fileStr = {};
                fileStr["name"] = file.name;
                fileStr["text"] = fileContent;
                fileStr["type"] = "file";
                fileStr["path"] = filePath + "/" + file.name;
                fileList.push(fileStr);
            }
        }
        if (entry.kind === "directory") {
            const newHandle = await dirHandle.getDirectoryHandle(entry.name, {
                create: false,
            });
            const newOut = (out[entry.name]={});
            const fileStr = {};
            fileStr["name"] = newHandle.name;
            fileStr["text"] = null;
            fileStr["type"] = "dir";
            fileStr["path"] = filePath + "/" + newHandle.name;
            fileList.push(fileStr);
            await handleDirectoryEntry(filePath + "/" + newHandle.name, newHandle, newOut, fileList);
        }
    }
}

$('body').on('click', '#mdBackButton', function() {
    var api_path = "/mdBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#mdFilesFolder").html(result);
            console.log("File repository(folder) fill");
        },
    });
});

$('body').on('click', '#htmlBackButton', function() {
    var api_path = "/htmlBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#htmlFilesFolder").html(result);
            console.log("File repository(folder) fill");
        },
    });
});