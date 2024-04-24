package com.example.SimpleWiki.repository;
import java.util.*;
import com.example.SimpleWiki.model.MDFile;

import scala.annotation.meta.field;

public class MDFileRepository {
    public FileRepository fileRepository;
    private List<MDFile> mdFiles;

    public List<MDFile> getMdFiles() { return this.mdFiles; }

    public MDFileRepository()
    {
        this.mdFiles = new ArrayList<MDFile>();
        this.fileRepository = new FileRepository();
    }

    public void AddFile(MDFile mdFile)
    {
        this.mdFiles.add(mdFile);
    }
}
