package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.model.HTMLFile;
import com.example.SimpleWiki.model.MDFile;

public class FileRepository {
    private String currentFolderPath;
    private String folderPath;
    private List<File> allFiles;
    private List<File> currentFiles;

    public FileRepository()
    {
        this.SetClearRepository();
    }

    public String GetCurrentFolderPath()
    {
        return this.currentFolderPath;
    }

    public List<File> GetAllFiles()
    {
        return this.allFiles;
    }

    public List<File> GetCurrentFiles()
    {
        return this.currentFiles;
    }

    public void SetCurrentFolder(String currentFolder) 
    {
        this.currentFolderPath = currentFolder;
    }

    public void SetAllFiles(List<File> allFiles) 
    {
        this.allFiles = allFiles;
    }

    public void SetFolderPath(String folderPath) {
        this.folderPath = folderPath;
    } 

    public void AddFile(String fileName, String fileText, String fileType, String filePath)
    {
        this.allFiles.add(new File(fileName, fileText, filePath, fileType));
    }

    public void AddFile(File file)
    {
        this.allFiles.add(file);
    }

    public void SetCurrentFiles()
    {  
        for(File file: this.allFiles)
        {
            if (file.GetPath().substring(0, file.GetPath().lastIndexOf("/")).equals(this.currentFolderPath))
            {
                this.currentFiles.add(file);
            }
        }
    }

    public void SetClearRepository() {
        this.allFiles = new ArrayList<File>();
        this.currentFolderPath = "";
        this.currentFiles = new ArrayList<File>();
    }

    public File GetFileByName(String name) {
        return this.currentFiles.stream().filter(file -> name.equals(file.GetName())).findFirst().orElse(null);
    }

    public void CurrentFilesUp(String name) {
        this.currentFiles = new ArrayList<File>();
        this.currentFolderPath += "/" + name;
        this.SetCurrentFiles();
    }

    public void CurrentFilesDown() {
        this.currentFiles = new ArrayList<File>();
        this.currentFolderPath = this.currentFolderPath.substring(0, this.currentFolderPath.lastIndexOf("/"));
        this.SetCurrentFiles();
    }

    public List<MDFile> GetMdFiles() {
        List<MDFile> listMd = new ArrayList<MDFile>() {};
        for (File file: this.allFiles)
        {
            if (file.GetType().equals("file"))
            {
                listMd.add(new MDFile(file.GetName(), file.GetText(),file.GetPath()));
            }
        }
        return listMd;
    }

    public void SetByRepository(FileRepository mdRepository, HTMLFileRepository htmlFileRepository, Map<File, File> complexFiles) {
        for (File fileMd: mdRepository.GetAllFiles()) 
        {
            if (fileMd.GetType().equals("dir"))
            {
                complexFiles.put(fileMd, fileMd);
            }
            else if (fileMd.GetType().equals("file"))
            {
                File newHtmlFile = new File(fileMd.GetName().split("\\.")[0] + ".html", "<h1>" + fileMd.GetName().split("\\.")[0] + "</h1>",fileMd.GetPath().split("\\.")[0] + ".html","file");
                complexFiles.put(fileMd, newHtmlFile);
            }
        }
        for (Map.Entry<File, File> entry : complexFiles.entrySet()) {
            if (entry.getKey().GetType().equals("dir"))
            {
                htmlFileRepository.fileRepository.AddFile(entry.getValue());
            }
            else if (entry.getKey().GetType().equals("file"))
            {
                htmlFileRepository.AddHtmlFile(entry.getValue().GetName().split("\\.")[0] + ".html", "<h1>" + entry.getValue().GetName().split("\\.")[0] + "</h1>",entry.getValue().GetPath().split("\\.")[0] + ".html");
                htmlFileRepository.fileRepository.AddFile(entry.getValue());
            }
        }
        this.currentFolderPath = this.folderPath;
        this.SetCurrentFiles();
    }

}
