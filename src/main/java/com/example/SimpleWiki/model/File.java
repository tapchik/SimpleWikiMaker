package com.example.SimpleWiki.model;

import javax.swing.text.html.HTML;

public class File {
    private String name;
    private String text;
    private String path;
    private String type;

    public String GetName() {
        return name;
    }

    public String GetText() {
        return text;
    }

    public String GetPath() {
        return path;
    }

    public String GetType() {
        return type;
    }

    public File(String name, String text, String path, String type)
    {
        this.name = name;
        this.text = text;
        this.path = path;
        this.type = type;
    }

    public MDFile FileToMd()
    {
        return new MDFile(this.GetName(), this.GetText(), this.GetPath());
    }

    public HTMLFile FileToHtml()
    {
        return new HTMLFile(this.GetName(), this.GetText(), this.GetPath());
    }
}
