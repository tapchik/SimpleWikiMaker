package com.example.SimpleWiki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.SimpleWiki.model.MDFile;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
    @RequestMapping("/button")
    @ResponseBody
    public String convertBtnClick(@RequestParam(value = "text") String text) {
        MDFile mdFile = new MDFile(text);
        return mdFile.ConvertToHtml(mdFile.text);
    }
}