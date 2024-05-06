package com.example.SimpleWiki.repository;
import java.util.*;
import com.example.SimpleWiki.model.MDFile;
import com.example.SimpleWiki.model.File;

public class MDFileRepository {
    public FileRepository fileRepository;
    private List<MDFile> mdFiles;

    public MDFileRepository()
    {
        this.mdFiles = new ArrayList<MDFile>();
        this.fileRepository = new FileRepository();
    }

    public List<MDFile> GetMdFiles() {
        List<MDFile> listMd = new ArrayList<MDFile>() {};
        for (File file: this.fileRepository.GetAllFiles())
        {
            if (file.GetType().equals("file"))
            {
                listMd.add(new MDFile(file.GetName(), file.GetText(),file.GetPath()));
            }
        }
        return listMd;
    }

    public void AddFile(MDFile mdFile)
    {
        this.mdFiles.add(mdFile);
    }
}