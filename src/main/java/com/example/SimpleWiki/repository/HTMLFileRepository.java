package com.example.SimpleWiki.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.SimpleWiki.model.HTMLFile;
import com.example.SimpleWiki.model.File;
import com.ibm.icu.text.MessageFormat;

public class HTMLFileRepository {
    public FileRepository fileRepository;
    private List<HTMLFile> listHtmlFiles;

    public HTMLFileRepository()
    {
        this.listHtmlFiles = new ArrayList<HTMLFile>();
        this.fileRepository = new FileRepository();
    }

    public List<HTMLFile> GetHtmlFiles() {
        return this.listHtmlFiles;
    }

    public void AddHtmlFile(HTMLFile file)
    {
        this.listHtmlFiles.add(file);
    }

    public HTMLFile FindByPath(String path) {
        for (HTMLFile htmlFile: this.listHtmlFiles)
        {
            if (htmlFile.GetPath().equals(path))
            {
                return htmlFile;
            }
        }
        return null;
    }

    public HTMLFile GetFileByPath(String path)
    {
        for (HTMLFile htmlFile: this.listHtmlFiles)
        {
            if (htmlFile.GetPath().equals(path))
            {
                return htmlFile;
            }
        }
        return null;
    }

    public void AddLinksToFiles()
    {
        for (HTMLFile htmlFile: this.listHtmlFiles)
        {
            String regex = "\\[\\[(([^\\[\\]\\n ])*)\\]\\]";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            String line = htmlFile.GetText();
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
            {
                System.out.println(matcher.group(1).split("/")[matcher.group(1).split("/").length - 1]);
                line = matcher.replaceAll(match -> MessageFormat.format("<a href={0}>{1}</a>", 
                "/p/"+match.group(1), match.group(1)));
                htmlFile.SetText(line);
            }            
        }
    }

    public String GetPathByName(String name) {
        return "/p" + this.fileRepository.GetCurrentFolderPath() + (this.fileRepository.GetCurrentFolderPath().equals("/") ? "" :
        "/") + name.split("\\.")[0];
    }

    public void SetDefaultFilesTheme(File themeFile) {
        for (HTMLFile htmlFile: this.listHtmlFiles) {
            htmlFile.SetTheme(themeFile);
        }
    }
}
