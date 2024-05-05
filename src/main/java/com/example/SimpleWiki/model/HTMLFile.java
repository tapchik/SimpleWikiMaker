package com.example.SimpleWiki.model;

public class HTMLFile {
    private String name;
    private String textTags;
    private String path;

    private File theme;

    public String GetName() {
        return this.name;
    }

    public String GetPath() {
        return this.path;
    }

    public String GetText() {
        return this.textTags;
    }

    public void SetText(String text) {
        this.textTags = text;
    }

    public void SetTheme(File theme) {
        this.theme = theme;
    }

    public File GetTheme() {
        return this.theme;
    }

    public HTMLFile(String name, String textTags, String path) {
        this.name = name;
        this.textTags = textTags;
        this.path = path;
    }
    
    public void AddTextTags(String text) {
        this.textTags += "\n" + text;
    }
}