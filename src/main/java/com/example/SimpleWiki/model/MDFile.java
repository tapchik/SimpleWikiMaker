package com.example.SimpleWiki.model;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

//Class for MDFile with atributes text and path (in the future for links)
public class MDFile {

    public String name;
    public String text;
    public String path;
    
    public MDFile(String text, String path) {
        this.text = text;
        this.path = path;
    }
    
    public MDFile(String name, String text, String path) {
        this.name = name;
        this.text = text;
        this.path = path;
    }

    public String ConvertToHtml() {
        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(this.text);
        String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        return htmlText;
    }
}
