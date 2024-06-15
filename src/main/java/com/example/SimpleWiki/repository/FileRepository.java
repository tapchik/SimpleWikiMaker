package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.FileObject;

public class FileRepository {
    private String currentFolderPath;
    private String folderPath;
    private List<FileObject> allFiles;
    private List<FileObject> currentFiles;

    public FileRepository()
    {
        this.SetClearRepository();
    }

    public String GetCurrentFolderPath()
    {
        return this.currentFolderPath;
    }

    public List<FileObject> GetAllFiles()
    {
        return this.allFiles;
    }

    public List<FileObject> GetCurrentFiles()
    {
        return this.currentFiles;
    }

    public void SetCurrentFolder(String currentFolder) 
    {
        this.currentFolderPath = currentFolder;
    }

    public void SetAllFiles(List<FileObject> allFiles) 
    {
        this.allFiles = allFiles;
    }

    public void SetFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    
    public FileObject GetFileByType(String type) {
        for (FileObject file: this.allFiles) {
            if (file.GetType().equals(type)) {
                return file;
            }
        }
        return null;
    }

    public FileObject GetFileByPath(String path)
    {
        for (FileObject file: this.allFiles)
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
        this.allFiles.add(new FileObject(fileName, fileText, filePath, fileType));
    }

    public void AddFile(FileObject file)
    {
        this.allFiles.add(file);
    }

    public void SetCurrentFiles()
    {  
        for(FileObject file: this.allFiles)
        {
            if (file.GetPath().substring(0, file.GetPath().lastIndexOf("/") == 0 ? 1 : file.GetPath().lastIndexOf("/")).equals(this.currentFolderPath))
            {
                this.currentFiles.add(file);
            }
        }
    }

    public void SetClearRepository() {
        this.allFiles = new ArrayList<FileObject>();
        this.currentFolderPath = "/";
        this.folderPath = "/";
        this.currentFiles = new ArrayList<FileObject>();
    }

    public void CurrentFilesUp(String name) {
        this.currentFiles = new ArrayList<FileObject>();
        this.currentFolderPath += this.currentFolderPath.equals("/") ? name : "/" + name;
        this.SetCurrentFiles();
    }

    public void CurrentFilesDown() {
        this.currentFiles = new ArrayList<FileObject>();
        this.currentFolderPath = this.currentFolderPath.substring(0, this.currentFolderPath.lastIndexOf("/") == 0 ? 1 : this.currentFolderPath.lastIndexOf("/"));
        this.SetCurrentFiles();
    }

    public void SetHtmlRepositoryByMd(FileRepository mdRepository, FileObject settings) {
        System.out.println("Degub stage: Extracting properties");
        for (FileObject fileMd: mdRepository.GetAllFiles()) 
        {
            if (fileMd.GetType().equals("file"))
            {
                FileObject htmlFile = new FileObject(fileMd.GetName().split("\\.")[0] + ".html", fileMd.GetText(), fileMd.GetPath().split("\\.")[0] + ".html", "file");
                String frontmatter = "";
                if (htmlFile.HasFrontmatter()) {
                    frontmatter = htmlFile.ExtractFrontmatter();
                    htmlFile.RemoveFrontmatter();
                }
                htmlFile.SetExtractedProperties(frontmatter);
                this.AddFile(htmlFile);
            }
            else
            {
                this.AddFile(fileMd);
            }
        }
        for (FileObject htmlFile: this.GetAllFiles())
        {
            if (htmlFile.GetType().equals("file"))
            {
                htmlFile.SetText(htmlFile.MdTextToHtml(GetFileProperties()));
                if (settings != null)
                {
                    String siteName = GetSiteName(settings);
                    String navigationTags = GetFilesNavigation(siteName, GetFileProperties());
                    htmlFile.AddNavigation(navigationTags);
                }
            }
        }
        this.currentFolderPath = this.folderPath;
        this.SetCurrentFiles();
    }

    public String GetFilesNavigation(String siteName, HashMap<String, HashMap<String,String>> props) {
        String navigationTags = "<nav class=\"navbar\">" + "\n"
                                + "<ul class=\"nav-list\">" + "\n";
        HashMap<Integer, String> fileNavigationNumbers = new HashMap<>();
        for (String pathKey: props.keySet())
        {
            if (!props.get(pathKey).containsKey("Navigation"))
            {
                continue;
            }
            if (props.get(pathKey).get("Navigation").equals(""))
            {
                continue;
            }
            String [] navProperty = props.get(pathKey).get("Navigation").split(";");
            if (navProperty.length > 2)
            {
                continue;
            }
            if (!navProperty[0].matches("\\d+"))
            {
                continue;
            }
            if (navProperty.length == 2)
            {
                fileNavigationNumbers.put(Integer.parseInt(navProperty[0]), pathKey + "|" + navProperty[1]);
            }
        }
        List<Integer> fileNavigationByKey = new ArrayList<Integer>(fileNavigationNumbers.keySet());
        Collections.sort(fileNavigationByKey);
        for (int i = 0; i < fileNavigationByKey.size(); i++)
        {
            if (i == 0 && !siteName.equals(""))
            {
                navigationTags += "<li><a class=\"relativeLink\" href=\"/Welcome\" id=siteName>" + siteName + "</a></li>" + "\n"; 
            }
            navigationTags += "<li><a class=\"relativeLink\" href=" +  fileNavigationNumbers.get(fileNavigationByKey.get(i)).split("\\|")[0].split("\\.")[0] + ">" 
            + fileNavigationNumbers.get(fileNavigationByKey.get(i)).split("\\|")[1] + "</a></li>" + "\n";
        }
        navigationTags += "</ul>" + "\n" +
                            "</nav>" + "\n";
        if (fileNavigationNumbers.size() == 0)
        {
            navigationTags = "";
        }
        return navigationTags;
    }

    public String GetSiteName(FileObject settings) {
        String siteName = "";
        for (String line: settings.GetText().split("\n"))
        {
            if (!(line.split(":").length == 2))
            {
                continue;
            }
            if (!(line.split(":")[0].equals("siteName")))
            {
                continue;
            }
            siteName = line.split(":")[1];
        }
        return siteName;
    }

    public HashMap<String, HashMap<String, String>> GetFileProperties() {
        HashMap<String, HashMap<String, String>> properties = new HashMap<String, HashMap<String,String>>();
        for (FileObject file: this.GetAllFiles())
        {
            if (file.GetType().equals("file"))
            {
                properties.put(file.GetPath(), file.GetProperties());
            }
        }
        return properties;
    }
}
