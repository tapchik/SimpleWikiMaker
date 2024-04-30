package com.example.SimpleWiki.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.model.HTMLFile;
import com.example.SimpleWiki.model.MDFile;
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

    public String FindByPath(String path) {
        for (HTMLFile htmlFile: this.listHtmlFiles)
        {
            if (htmlFile.GetPath().equals(path))
            {
                return htmlFile.GetText();
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
            //System.out.println(htmlFile.GetPath());
            if (matcher.find())
            {
                System.out.println(matcher.group(1));
                line = matcher.replaceAll(match -> MessageFormat.format("<a href={0}>{1}</a>", 
                "/p/"+match.group(1), match.group(1)));
                htmlFile.SetText(line);
            }            
        }
    }
}
