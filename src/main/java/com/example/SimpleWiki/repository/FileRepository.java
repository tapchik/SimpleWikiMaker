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

    public void SetHtmlRepositoryByMd(FileRepository mdRepository, File settings) {
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
                htmlFile.SetExtractedProperties(frontmatter);

                this.AddFile(htmlFile);
            }
            else
            {
                this.AddFile(fileMd);
            }
        }
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
        String navigationTags = GetFilesNavigation(siteName, GetFileProperties());
        for (File htmlFile: this.GetAllFiles())
        {
            if (htmlFile.GetType().equals("file"))
            {
                htmlFile.SetText(htmlFile.MdTextToHtml(GetFileProperties()));
                htmlFile.AddNavigation(navigationTags);
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
                navigationTags += "<li><a id=siteName>" + siteName + "</a></li>" + "\n"; 
            }
            navigationTags += "<li><a href=" + "/p" +  fileNavigationNumbers.get(fileNavigationByKey.get(i)).split("\\|")[0].split("\\.")[0] + ">" 
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

    public HashMap<String, HashMap<String, String>> GetFileProperties() {
        HashMap<String, HashMap<String, String>> properties = new HashMap<String, HashMap<String,String>>();
        for (File file: this.GetAllFiles())
        {
            if (file.GetType().equals("file"))
            {
                properties.put(file.GetPath(), file.GetProperties());
            }
        }
        return properties;
    }
}
