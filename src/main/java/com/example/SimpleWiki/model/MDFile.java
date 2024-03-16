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
        tagsOpen.put("h", false);
        return tagsOpen;
    }

    public String ConvertToHtml(String text) {
        for (String line: text.split("\n"))
        {
            line = FindCodeBlock(line);
            if (!tagsOpen.get("```") && line != "</code></pre>")
            {
                line = FindEmptyString(line);
                line = FindCodeInline(line);
                line = FindBold(line);
                line = FindItalic(line);
                line = FindStrikethrough(line);
                line = FindHighlighted(line);
                line = FindHeading(line);
                if (tagsOpen.get("h"))
                {
                    MultiLineCheck();
                    result = result + line + "\n";
                    tagsOpen.put("h", false);
                }
                else
                {
                    multiLine = multiLine + line + "\n";
                }
            }
            else
            {
                result = result + line + "\n";
            }
        }
        MultiLineCheck();
        return result + multiLine;
    }

    private String FindEmptyString(String line) {
        if (line == "")
        {
            MultiLineCheck();
            result = result + "\n";
            return line;
        }
        return line;
    }

    public void MultiLineCheck() {
        if (multiLine.trim() != "")
        {
            multiLine = FindCodeInline(multiLine);
            multiLine = FindBold(multiLine);
            multiLine = FindItalic(multiLine);
            multiLine = FindStrikethrough(multiLine);
            multiLine = FindHighlighted(multiLine);
            multiLine = "<p>" + multiLine.trim() + "</p>" + "\n";
            result = result + multiLine;
            multiLine = "";
        }
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

    public String FindCodeBlock(String line) {
        String regex = !tagsOpen.get("```") ? "^```([^`]*)$" : "^```$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find())
        {
            if (!tagsOpen.get("```"))
            {
                String replace = "<pre><code>";
                tagsOpen.put("```", true);
                MultiLineCheck();
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

    public String FindHeading(String line) {
        String regex = "^#{1,6} (.+)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find())
        { 
            line = matcher.replaceAll(match -> "<h" + match.group(0).split(" ")[0].length() + ">" + match.group(1) + "</h" + match.group(0).split(" ")[0].length() + ">");
            tagsOpen.put("h", true);
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
