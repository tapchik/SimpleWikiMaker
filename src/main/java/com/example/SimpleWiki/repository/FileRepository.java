package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.File;

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

    public void SetCurrentFolder(String folder) 
    {
        this.currentFolderPath = folder;
    }

    public void SetAllFiles(List<File> allFiles) 
    {
        this.allFiles = allFiles;
    }

    public void SetFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    
    public File GetFileByType(String type) {
        for (File file: this.allFiles) {
            if (file.GetType().equals(type)) {
                return file;
            }
        }
        return null;
    }

    public File GetFileByPath(String path)
    {
        for (File file: this.allFiles)
        {
            if (file.GetPath().replaceAll(" ", "%20").equals(path))
            {
                return file;
            }
        }
        return null;
    }

    public String GetPathByName(String name) {
        return "/p" + this.GetCurrentFolderPath() + (this.GetCurrentFolderPath().equals("/") ? "" :
        "/") + name.split("\\.")[0];
    }

    public void AddFile(String fileName, String fileText, String fileType, String filePath)
    {
        File file = new File(fileName, fileText, filePath, fileType);
        this.allFiles.add(file);
    }

    public void AddFile(File file)
    {
        this.allFiles.add(file);
    }

    public void SetCurrentFiles()
    {  
        for(File file: this.allFiles)
        {
            if (file.GetPath().substring(0, file.GetPath().lastIndexOf("/") == 0 ? 1 : file.GetPath().lastIndexOf("/")).equals(this.currentFolderPath))
            {
                this.currentFiles.add(file);
            }
        }
    }

    private void SetClearRepository() {
        this.allFiles = new ArrayList<File>();
        this.currentFolderPath = "/";
        this.folderPath = "/";
        this.currentFiles = new ArrayList<File>();
    }

    public void CurrentFilesUp(String name) {
        this.currentFiles = new ArrayList<File>();
        this.currentFolderPath += this.currentFolderPath.equals("/") ? name : "/" + name;
        this.SetCurrentFiles();
    }

    public void CurrentFilesDown() {
        this.currentFiles = new ArrayList<File>();
        this.currentFolderPath = this.currentFolderPath.substring(0, this.currentFolderPath.lastIndexOf("/") == 0 ? 1 : this.currentFolderPath.lastIndexOf("/"));
        this.SetCurrentFiles();
    }

    public void SetHtmlRepositoryByMd(FileRepository mdRepository, Map<String, Boolean> siteSettings) {
        for (File fileMd: mdRepository.GetAllFiles()) 
        {
            this.AddFile(fileMd.FileToHtml());
            for (Map.Entry<String, String> property : fileMd.GetProperties().entrySet()) {
                // log to console properties that were found, good for testing
                String log = String.format("File %s has property %s: %s", fileMd.GetName(), property.getKey(), property.getValue());
                System.out.println(log);
            }
        }
        this.currentFolderPath = this.folderPath;
        this.SetCurrentFiles();
    }
}
