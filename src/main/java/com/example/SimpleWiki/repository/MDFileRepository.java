package com.example.SimpleWiki.repository;
import java.util.*;

import com.example.SimpleWiki.model.MDFile;

public class MDFileRepository {
    String start;
    List<MDFile> mdFiles;

    public MDFileRepository()
    {
        mdFiles = new ArrayList<MDFile>();
        start = "";
    }
}
