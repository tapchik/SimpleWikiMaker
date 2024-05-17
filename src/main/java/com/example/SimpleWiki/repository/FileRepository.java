package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.File;

public class FileRepository {
    private String currentFolderPath;
    private String folderPath;
    private List<File> allFiles;
    private List<File> currentFiles;
    private HashMap<String, HashMap<String, String>> filesPropertys;

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
        return "/f" + this.GetCurrentFolderPath() + (this.GetCurrentFolderPath().equals("/") ? "" :
        "/") + name.split("\\.")[0];
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

    public void SetHtmlRepositoryByMd(FileRepository mdRepository) {
        this.filesPropertys = new HashMap<String, HashMap<String,String>>();
        System.out.println("Degub stage: Extracting properties");
        for (File fileMd: mdRepository.GetAllFiles()) 
        {
            if (fileMd.GetType().equals("file"))
            {
                File htmlFile = new File(fileMd.GetName().split("\\.")[0] + ".html", fileMd.GetText(), fileMd.GetPath().split("\\.")[0] + ".html", "file");
                String frontmatter = "";
                if (htmlFile.HasFrontmatter()) {
                    frontmatter = htmlFile.ExtractFrontmatter();
                    htmlFile.RemoveFrontmatter();
                }
                HashMap<String, String> props = htmlFile.ExtractProperties(frontmatter);
                this.filesPropertys.put(htmlFile.GetPath(), props);
                this.AddFile(htmlFile);
            }
            else
            {
                this.AddFile(fileMd);
            }
        }
        for (File htmlFile: this.GetAllFiles())
        {
            if (htmlFile.GetType().equals("file"))
            {
                htmlFile.SetText(htmlFile.MdTextToHtml(this.filesPropertys));
            }
        }
        this.currentFolderPath = this.folderPath;
        this.SetCurrentFiles();
    }
}
