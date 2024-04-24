package com.example.SimpleWiki.model;

public class HTMLFile {
    private String name;
    private String textTags;
    private String path;

    public String GetPath() {
        return this.path;
    }

    public String GetText() {
        return this.textTags;
    }

    public HTMLFile(String name, String textTags, String path) {
        this.name = name;
        this.textTags = textTags;
        this.path = path;
    }
    
    public String AddDefaultTagsStart() {
        return "";
    }

    public String AddDefaultTagsEnd() {
        return "";
    }

    public void AddTextTags(String text) {
        this.textTags += "\n" + text;
    }
}