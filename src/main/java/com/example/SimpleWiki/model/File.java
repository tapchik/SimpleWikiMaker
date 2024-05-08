package com.example.SimpleWiki.model;

import com.vladsch.flexmark.util.ast.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.MessageFormat;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

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
            Node document = parser.parse(this.text);
            String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
            File htmlFile = new File(this.GetName().split("\\.")[0] + ".html", htmlText, this.GetPath().split("\\.")[0] + ".html", this.GetType());
            htmlFile.AddLinks();
            return htmlFile;
        }
        return this;
    }

    public void AddLinks()
    {
        String regex = "\\[\\[(([^\\[\\]\\n ])*)\\]\\]";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        String line = this.GetText();
        Matcher matcher = pattern.matcher(line);
        if (matcher.find())
        {
            line = matcher.replaceAll(match -> MessageFormat.format("<a href={0}>{1}</a>", 
            "/p/"+ (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[0] : match.group(1)), 
            (match.group(1).indexOf("|") != -1 ? match.group(1).split("\\|")[1] 
            : (match.group(1).indexOf("/") != -1 ? match.group(1).substring(match.group(1).lastIndexOf("/")+1) : match.group(1)))));
            this.text = line;
        }
    }
}
