package com.example.SimpleWiki.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.model.HTMLFile;
import com.example.SimpleWiki.model.MDFile;

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

    public void AddHtmlFile(String newName, String newText, String newPath) {
        this.listHtmlFiles.add(new HTMLFile(newName, newText, newPath));
    }
}
