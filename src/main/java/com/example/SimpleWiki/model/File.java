package com.example.SimpleWiki.model;

import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.MessageFormat;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class File {
    private String name;
    private String text;
    private String path;
    private String type;
    private HashMap<String,String> properties;

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

    public HashMap<String,String> GetProperties() {
        return properties;
    }

    public void SetText(String text) {
        this.text = text;
    }

    public void SetProperties(HashMap<String,String> properties) {
        this.properties = properties;
    }

    public File(String name, String text, String path, String type)
    {
        this.name = name;
        this.text = text;
        this.path = path;
        this.type = type;
    }

    public String MdTextToHtml(HashMap<String, HashMap<String,String>> props)
    {
        //set options
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        //initialise parser and renderer with your options
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        //get text from markdown to html with parser and all options
        String htmlText = this.text;
        htmlText = AddQuery(htmlText, props);
        Node document = parser.parse(htmlText);
        htmlText = renderer.render(document); 
        htmlText = AddLinks(htmlText);
        return htmlText;
    }

    public Boolean HasFrontmatter() {
        // text must have 3 or more characters
        if (this.text.length() < 3)
            return false;
        // text must start with three dashes
        if (!this.text.substring(0, 3).equals("---"))
            return false;
        // text must have three dashes on separate lines twice with some other text in between
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.text);
        if (!matcher.find())
            return false;
        // if all is checked, then yes, this.text has a frontmatter
        return true;
    }

    public String ExtractFrontmatter() {
        // coppies out frontmatter from this.text
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.text);
        matcher.find();
        String frontmatter = matcher.group();
        return frontmatter;
    }

    public void RemoveFrontmatter() {
        // attempts to remove frontmatter from this.text
        String regex = "-{3}\n(.+?\n)+-{3}\n";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.text);
        matcher.find();
        this.text = matcher.replaceFirst("");
    }

    public void SetExtractedProperties(String frontmatter) {
        // atempts to extract properties from this.text
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
        int count = 0;
        while (matcher.find()) {
            String prop = matcher.group();
            String key = prop.split(": ")[0];
            String value = prop.split(": ")[1];
            properties.put(key, value);
            count++;
            // logging found properies, good for testing
            String log = String.format("File %s has property, %s: %s", this.name, key, value);
            System.out.println(log);
        }
        if (count==0 && !HasFrontmatter())
            System.out.println(String.format("File %s has no properties", this.name));
        SetProperties(properties);
    }

    private String AddQuery(String htmlText, HashMap<String, HashMap<String, String>> props) {
        HashMap<String, String> queryProps = new HashMap<String, String>();
        String regex = "^```query\\n((\\[([^\\[\\]\\n])+:([^\\[\\]\\n])*\\]\\n)+)```$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(htmlText);
        while (matcher.find())
        {
            String pageLinks = "<ul>";
            for (String propLine: matcher.group(1).split("\n"))
            {
                String line = propLine.substring(1, propLine.length()-1);
                queryProps.put(line.split(":")[0], line.split(":").length == 1 ? "" : line.split(":")[1]);
            }
            for (String pathKey: props.keySet())
            {
                Boolean add = true;
                for (String queryPropKey: queryProps.keySet())
                {
                    if (props.get(pathKey).containsKey(queryPropKey))
                    {
                        if (!props.get(pathKey).get(queryPropKey).equals(queryProps.get(queryPropKey)))
                        {
                            add = false;
                        }
                    }
                    else
                    {
                        add = false;
                    }
                }
                if (add)
                {
                    pageLinks += "<li class=\"dynamicListLi\"><a href=/p" + pathKey.split("\\.")[0].replaceAll(" ", "%20") + ">" 
                    + (props.get(pathKey).containsKey("PathAlias") ? props.get(pathKey).get("PathAlias") : pathKey.split("\\.")[0]) + "</a></li>";
                }
            }
            pageLinks = "<div class=\"dynamicListDiv\">" + pageLinks + "</div>";
            htmlText = matcher.replaceFirst(pageLinks);
            matcher = pattern.matcher(htmlText);
        }
        return htmlText;
    }

    public String AddLinks(String htmlText)
    {
        String regex = "\\[\\[([^\\[\\]\\n]+)\\]\\]";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(htmlText);
        while (matcher.find())
        {
            if (!matcher.group(1).trim().equals(""))
            {
                htmlText = matcher.replaceFirst(match -> MessageFormat.format("<a href={0}>{1}</a>", 
                "/p/"+ (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[0].replaceAll(" ", "%20") 
                : match.group(1).replaceAll(" ", "%20")), 
                (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[1] : match.group(1))));
                matcher = pattern.matcher(htmlText);
            }
        }
        return htmlText;
    }

    public void AddNavigation(String navigationTags) {
        this.text = navigationTags + this.text;
    }
}
