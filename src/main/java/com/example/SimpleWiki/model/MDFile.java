package com.example.SimpleWiki.model;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MDFile {
    String text;
    
    public MDFile(String text)
    {
        this.text = text;
    }

    public void ConvertToHtml(String text)
    {
        String result = "";
        String [] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            
        }
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
