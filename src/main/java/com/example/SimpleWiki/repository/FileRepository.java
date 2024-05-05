package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.model.HTMLFile;

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
    
    public File GetFileByType(String type) {
        for (File file: this.allFiles) {
            if (file.GetType().equals(type)) {
                return file;
            }
        }
        return null;
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
            if (file.GetPath().substring(0, file.GetPath().lastIndexOf("/") == 0 ? 1 : file.GetPath().lastIndexOf("/")).equals(this.currentFolderPath))
            {
                this.currentFiles.add(file);
            }
        }
    }

    public void SetClearRepository() {
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

    public void SetByRepository(MDFileRepository mdRepository, HTMLFileRepository htmlFileRepository) {
        for (File fileMd: mdRepository.fileRepository.GetAllFiles()) 
        {
            if (fileMd.GetType().equals("dir"))
            {
                htmlFileRepository.fileRepository.AddFile(fileMd);
            }
            else if (fileMd.GetType().equals("file"))
            {
                File newHtmlFile = new File(fileMd.GetName().split("\\.")[0] + ".html", "<h1>" + fileMd.GetName().split("\\.")[0] + "</h1>",fileMd.GetPath().split("\\.")[0] + ".html","file");
                HTMLFile fileHtml = newHtmlFile.FileToHtml();
                fileHtml.AddTextTags(fileMd.FileToMd().ConvertToHtml());
                htmlFileRepository.AddHtmlFile(fileHtml);
                htmlFileRepository.fileRepository.AddFile(new File(fileHtml.GetName(), fileHtml.GetText(), fileHtml.GetPath(), "file"));
            }
        }
        this.currentFolderPath = this.folderPath;
        this.SetCurrentFiles();
    }

}
