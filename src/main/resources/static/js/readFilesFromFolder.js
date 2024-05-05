$('body').on('click', '#pickFilesButton', async (evt) => {
    const out = {};
    const dirHandle = await showDirectoryPicker();
    const fileList = [];
    const settingsList = [];
    await handleDirectoryEntry("", dirHandle, out, fileList, settingsList);
    var apiPath = "/setListOfMdFiles";
    $.ajax({
        type: "POST",
        url: apiPath,
        data: JSON.stringify(fileList),
        contentType:"application/json; charset=utf-8",
        dataType: "text",
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill");
            console.log(fileList);

            var apiPath = "/setSettingsFiles";
            $.ajax({
                type: "POST",
                url: apiPath,
                data: JSON.stringify(settingsList),
                contentType:"application/json; charset=utf-8",
                dataType: "text",
                success:function() {
                    console.log(settingsList);
                },
            });
        },
    });
});

async function handleDirectoryEntry(filePath, dirHandle, out, fileList, settingsList) {
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
            else if (file.name === "theme.css") {
                const fileContent = await file.text();
                const settingStr = {};
                settingStr["name"] = file.name;
                settingStr["text"] = fileContent;
                settingStr["type"] = "styleCSS";
                settingsList.push(settingStr);
            }
        }
        if (entry.kind === "directory") {
            const newHandle = await dirHandle.getDirectoryHandle(entry.name, {
                create: false,
            });
            if (newHandle.name !== ".obsidian")
            {
                const newOut = (out[entry.name]={});
                const fileStr = {};
                fileStr["name"] = newHandle.name;
                fileStr["text"] = null;
                fileStr["type"] = "dir";
                fileStr["path"] = filePath + "/" + newHandle.name;
                fileList.push(fileStr);
                await handleDirectoryEntry(filePath + "/" + newHandle.name, newHandle, newOut, fileList, settingsList);
            }
        }
    }
}

$('body').on('click', '#mdBackButton', function() {
    var api_path = "/mdBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill");
        },
    });
});

$('body').on('click', '#htmlBackButton', function() {
    var api_path = "/htmlBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#htmlFiles").html(result);
            console.log("HTML files fill");
        },
    });
});