package com.example.SimpleWiki.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.example.SimpleWiki.model.MDFile;
import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.repository.HTMLFileRepository;
import com.example.SimpleWiki.repository.MDFileRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    MDFileRepository mdRepository;
    HTMLFileRepository htmlRepository;
    Map<File, File> complexFiles;

    public HomeController()
    {
        mdRepository = new MDFileRepository();
        htmlRepository = new HTMLFileRepository();
        complexFiles = new HashMap<File,File>();
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    @RequestMapping("/convertButtonClick")
    @ResponseBody
    public String ConvertBtnClick(@RequestParam(value = "text") String text) {
        MDFile mdFile = new MDFile(text, "");
        return mdFile.ConvertToHtml();
    }

    @RequestMapping("/mdDirButtonClick")
    public String MdDirClick(Model model, @RequestParam(value = "name") String name)
    {
        mdRepository.fileRepository.CurrentFilesUp(name);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        model.addAttribute("showMdBackButton", false);
        return "fragments/listOfMdFiles";
    }
    @RequestMapping("/mdFileButtonClick")
    @ResponseBody
    public String MdFileClick(Model model, @RequestParam(value = "name") String name)
    {
        File file = mdRepository.fileRepository.GetFileByName(name);
        return file.GetText();
    }

    @RequestMapping("/htmlDirButtonClick")
    public String HtmlDirClick(Model model, @RequestParam(value = "name") String name)
    {
        htmlRepository.fileRepository.CurrentFilesUp(name);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        model.addAttribute("showHtmlBackButton", false);
        return "fragments/listOfHtmlFiles";
    }
    @RequestMapping("/htmlFileButtonClick")
    @ResponseBody
    public String HtmlFileClick(Model model, @RequestParam(value = "name") String name)
    {
        File file = htmlRepository.fileRepository.GetFileByName(name);
        return file.GetText();
    }

    @RequestMapping("/setCurrentFolder")
    @ResponseBody
    public String SetCurrentFolder(@RequestParam(value = "dirHandle") String currentFolder)
    {
        mdRepository = new MDFileRepository();
        htmlRepository = new HTMLFileRepository();
        mdRepository.fileRepository.SetFolderPath(currentFolder);
        htmlRepository.fileRepository.SetFolderPath(currentFolder);
        complexFiles = new HashMap<File,File>();
        mdRepository.fileRepository.SetCurrentFolder(currentFolder);
        return "Current folder is set";
    }

    @RequestMapping("/listOfMdFiles")
    public String SetCurrentFiles(Model model, @RequestBody List<File> allFiles) {
        mdRepository.fileRepository.SetAllFiles(allFiles);
        mdRepository.fileRepository.SetCurrentFiles();
        model.addAttribute("showMdBackButton", true);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/mdBackButtonClick")
    public String MdSetCurrentFilesDown(Model model) {
        mdRepository.fileRepository.CurrentFilesDown();
        model.addAttribute("showMdBackButton", mdRepository.fileRepository.GetCurrentFolderPath().indexOf("/") == -1 ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlBackButtonClick")
    public String HtmlSetCurrentFilesDown(Model model) {
        htmlRepository.fileRepository.CurrentFilesDown();
        model.addAttribute("showHtmlBackButton", htmlRepository.fileRepository.GetCurrentFolderPath().indexOf("/") == -1 ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/convertRepoToHtml")
    public String ConvertMdFilesToHtml(Model model) {
        complexFiles = new HashMap<File,File>();
        htmlRepository.fileRepository.SetByRepository(mdRepository.fileRepository, htmlRepository, complexFiles);
        htmlRepository.AddLinksToFiles();
        model.addAttribute("showHtmlBackButton", true);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, value = "/p/**")
    @ResponseBody
    public String newHtmlPage(HttpServletRequest request) {
        String restOfTheUrl = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());
        //System.out.println(restOfTheUrl);
        String textTags = htmlRepository.FindByPath(restOfTheUrl+".html");
        if (textTags == null)
        {
            return "<html>\n" + "<header><title>Welcome</title></header>\n" +
          "<body>\n" + "File doesnt exist" + "\n" + "</body>\n" + "</html>";
        }
        return "<html>\n" + "<header><title>Welcome</title></header>\n" +
          "<body>\n" + textTags + "\n" + "</body>\n" + "</html>";
    }

}