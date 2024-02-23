package com.example.SimpleWiki.model;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MDFile {
    public String text;
    
    public MDFile(String text)
    {
        this.text = text;
    }

    public String ConvertToHtml(String text)
    {
        String result = "OK";
        String [] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            
        }
        return "Read Success";
    }

    public void AddDefaultHtml(String result)
    {
        result += "<!DOCTYPE html>\n";
        result += "<html>\n";
        result += "<head>\n";
        
    }

    public void FindCodeBlocks()
    {
        Pattern ptrn = Pattern.compile("");
    }
}
