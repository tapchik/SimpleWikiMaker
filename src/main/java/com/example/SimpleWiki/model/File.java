package com.example.SimpleWiki.model;

import com.vladsch.flexmark.util.ast.Node;

import java.util.HashMap;   
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

    public void SetText(String text) {
        this.text = text;
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
        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        String htmlText = this.text;
        htmlText = AddQuery(htmlText, props);
        Node document = parser.parse(htmlText);
        htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n" 
        htmlText = AddLinks(htmlText);
        return htmlText;
    }

    private String AddQuery(String htmlText, HashMap<String, HashMap<String, String>> props) {
        HashMap<String, String> queryProps = new HashMap<String, String>();
        String regex = "^```query\\n((\\[([^\\[\\]\\n])+:([^\\[\\]\\n])*\\]\\n)+)```$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(htmlText);
        while (matcher.find())
        {
            String replacement = "";
            for (String currentLine: matcher.group(1).split("\n"))
            {
                String line = currentLine.substring(1, currentLine.length()-1);
                System.out.println(line);
                queryProps.put(line.split(":")[0], line.split(":").length == 1 ? "" : line.split(":")[1]);
            }
            for (String keyPath: props.keySet())
            {
                Boolean add = true;
                for (String queryKeyProp: queryProps.keySet())
                {
                    if (props.get(keyPath).containsKey(queryKeyProp))
                    {
                        if (!props.get(keyPath).get(queryKeyProp).equals(queryProps.get(queryKeyProp)))
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
                    replacement += "<a href=/p" + keyPath.split("\\.")[0] + ">" + keyPath.split("\\.")[0] + "</a>\n";
                }
            }
            htmlText = matcher.replaceFirst(replacement);
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

    public HashMap<String, String> FindProperties()
    {
        HashMap<String, String> properties = new HashMap<String,String>();
        String regex = "^---\\n(((([^\\[\\]\\n])+):([^\\[\\]\\n])*\\n)+)---$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.text);
        if (matcher.find())
        {
            for (String currentLine: matcher.group(1).split("\n"))
            {
                properties.put(currentLine.split(": ")[0], currentLine.split(": ").length == 1 ? "" : currentLine.split(": ")[1]);
            }
            this.text = matcher.replaceFirst("");
            return properties;
        }
        return null;
    }
}
