package com.example.SimpleWiki.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.MessageFormat;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class File {
    private String name;
    private String text;
    private String path;
    private String type;
    private Map<String, String> properties;

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

    public Map<String, String> GetProperties()
    {
        return properties;
    }

    public File(String name, String text, String path, String type)
    {
        this.name = name;
        this.text = text;
        this.path = path;
        this.type = type;
    }

    public File FileToHtml()
    {
        if (this.GetType().equals("file"))
        {
            MutableDataSet options = new MutableDataSet();

            // uncomment to set optional extensions
            //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

            // uncomment to convert soft-breaks to hard breaks
            options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            // You can re-use parser and renderer instances
            String htmlText = this.text;
            // extract properties from frontmatter, if possible
            if (HasFrontmatter(htmlText)) {
                String frontmatter = ExtractFrontmatter(htmlText);
                htmlText = RemoveFrontmatter(htmlText);
                this.properties = ExtractProperties(frontmatter);
            }
            Node document = parser.parse(htmlText);
            htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n" 
            File htmlFile = new File(this.GetName().split("\\.")[0] + ".html", htmlText, this.GetPath().split("\\.")[0] + ".html", this.GetType());
            htmlFile.AddLinks();
            return htmlFile;
        }
        return this;
    }

    public void AddLinks()
    {
        String regex = "\\[\\[([^\\[\\]\\n]+)\\]\\]";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        String line = this.text;
        Matcher matcher = pattern.matcher(line);
        while (matcher.find())
        {
            if (!matcher.group(1).trim().equals(""))
            {
                line = matcher.replaceFirst(match -> MessageFormat.format("<a href={0}>{1}</a>", 
                "/p/"+ (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[0].replaceAll(" ", "%20") 
                : match.group(1).replaceAll(" ", "%20")), 
                (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[1] : match.group(1))));
                matcher = pattern.matcher(line);
            }
        }
        this.text = line;
    }
    
    public Boolean HasFrontmatter(String originalText) {
        // text must have 3 or more characters
        if (originalText.length() < 3)
            return false;
        // text must start with three dashes
        if (!originalText.substring(0, 3).equals("---"))
            return false;
        // text must have three dashes on separate lines twice with some other text in between
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(originalText);
        if (!matcher.find())
            return false;
        // if all is checked
        return true;
    }

    public String ExtractFrontmatter(String originalText) {
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(originalText);
        matcher.find();
        String frontmatter = matcher.group();
        return frontmatter;
    }

    public String RemoveFrontmatter(String originalText) {
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(originalText);
        matcher.find();
        String alteredText = matcher.replaceFirst("");
        return alteredText;
    }

    public HashMap<String, String> ExtractProperties(String frontmatter) {
        HashMap<String, String> properties = new HashMap<>();
        // pealing away three dashes in the begining and end
        if (frontmatter.startsWith("---"))
            frontmatter = frontmatter.substring(3);
        if (frontmatter.endsWith("---"))
            frontmatter = frontmatter.substring(0, frontmatter.length()-3);
        // extracting properties
        String regex = "^(.+): (.+)$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(frontmatter);
        while (matcher.find()) {
            String prop = matcher.group();
            String key = prop.split(": ")[0];
            String value = prop.split(": ")[1];
            properties.put(key, value);
        }
        return properties;
    }
}
