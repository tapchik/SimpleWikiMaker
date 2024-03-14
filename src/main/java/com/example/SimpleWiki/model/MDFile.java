package com.example.SimpleWiki.model;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Class for MDFile with atributes text and path (in the future for links)
public class MDFile {

    public String text;
    //public String path; //for path and connections
    public HashMap<String, Boolean> tagsOpen;
    public String multiLine;
    public String result;
    
    public MDFile(String text) { 
        this.text = text;
        //this.path = path;
        this.tagsOpen = TagsOpenFill(tagsOpen);
        this.multiLine = "";
        this.result = "";
    }

    public HashMap<String, Boolean> TagsOpenFill(HashMap<String, Boolean> tagsOpen) {
        tagsOpen = new HashMap<String, Boolean>();
        tagsOpen.put("```", false);
        return tagsOpen;
    }

    public String ConvertToHtml(String text) {

        String result = "";
        for (String line: text.split("\n"))
        {
            line = FindCodeBlock(line);
            if (!tagsOpen.get("```"))
            {
                line = FindEmptyString(line);
                line = FindCodeInline(line);
                line = FindBold(line);
                line = FindItalic(line);
                line = FindStrikethrough(line);
                line = FindHighlighted(line);
                multiLine = multiLine + line;
            }
            else
            {
                result = result + line;
            }
        }
        return result + multiLine;
    }

    private String FindHighlighted(String line) {
        String regex = "==([^=]+)==";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        String lineCopy = line;
        if (matcher.find())
        {
            line = matcher.replaceAll(match -> lineCopy.matches("<code>(.*)" + Pattern.quote(match.group(0)) + "(.*)</code>") ? match.group(0) : "<span>" + match.group(1) + "</span>");
        }
        return line;
    }

    private String FindStrikethrough(String line) {
        String regex = "~~([^~]+)~~";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        String lineCopy = line;
        if (matcher.find())
        {
            line = matcher.replaceAll(match -> lineCopy.matches("<code>(.*)" + Pattern.quote(match.group(0)) + "(.*)</code>") ? match.group(0) : "<s>" + match.group(1) + "</s>");
        }
        return line;
    }

    private String FindItalic(String line) {
        String regex = "\\*([^\\*]+)\\*";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        String lineCopy = line;
        if (matcher.find())
        {
            line = matcher.replaceAll(match -> lineCopy.matches("<code>(.*)" + Pattern.quote(match.group(0)) + "(.*)</code>") ? match.group(0) : "<em>" + match.group(1) + "</em>");
        }
        return line;
    }

    private String FindBold(String line) {
        String regex = "\\*\\*([^\\*]+)\\*\\*";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        String lineCopy = line;
        if (matcher.find())
        {
            line = matcher.replaceAll(match -> lineCopy.matches("<code>(.*)" + Pattern.quote(match.group(0)) + "(.*)</code>") ? match.group(0) : "<b>" + match.group(1) + "</b>");
        }
        return line;
    }

    public String FindCodeBlock(String line) {
        String regex = !tagsOpen.get("```") ? "^```([^`]*)$" : "^```$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find())
        {
            if (!tagsOpen.get("```"))
            {
                String replace = "<pre><code>\n";
                tagsOpen.put("```", true);
                return replace;
            }
            else
            {
                String replace = "</code></pre>";
                tagsOpen.put("```", false);
                return replace;
            }
        }
        else
        {
            return line; 
        } 
    }

    private String FindCodeInline(String line) {
        String regex = "`([^`]+)`";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find())
        { 
            line = matcher.replaceAll(match -> "<code>" + match.group(1) + "</code>");
        }
        return line;
    }

    private String FindEmptyString(String line) {
        if (line == "")
        {
            
        }
        return line;
    }

    public String AddDefaultTagsStart() {
        return "";
    }

    public String AddDefaultTagsEnd() {
        return "";
    }
}
