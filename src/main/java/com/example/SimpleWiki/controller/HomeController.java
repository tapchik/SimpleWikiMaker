package com.example.SimpleWiki.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.model.HTMLFile;
import com.example.SimpleWiki.repository.FileRepository;
import com.example.SimpleWiki.repository.HTMLFileRepository;
import com.example.SimpleWiki.repository.MDFileRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    MDFileRepository mdRepository;
    HTMLFileRepository htmlRepository;

    FileRepository settingsRepository;

    public HomeController()
    {
        mdRepository = new MDFileRepository();
        htmlRepository = new HTMLFileRepository();
        settingsRepository = new FileRepository();
    }

    @RequestMapping("/")
    public String index(Model model) 
    {
        return "index";
    }

    @RequestMapping("/mdDirButtonClick")
    public String MdDirClick(Model model, @RequestParam(value = "name") String name)
    {
        mdRepository.fileRepository.CurrentFilesUp(name);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        model.addAttribute("showMdBackButton", false);
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlDirButtonClick")
    public String HtmlDirClick(Model model, @RequestParam(value = "name") String name)
    {
        htmlRepository.fileRepository.CurrentFilesUp(name);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        model.addAttribute("showHtmlBackButton", false);
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/setListOfMdFiles")
    public String SetCurrentFiles(Model model, @RequestBody List<File> allFiles) 
    {
        mdRepository = new MDFileRepository();
        htmlRepository = new HTMLFileRepository();

        mdRepository.fileRepository.SetAllFiles(allFiles);
        mdRepository.fileRepository.SetCurrentFiles();
        model.addAttribute("showMdBackButton", true);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    } 

    @RequestMapping("/mdBackButtonClick")
    public String MdSetCurrentFilesDown(Model model) 
    {
        mdRepository.fileRepository.CurrentFilesDown();
        model.addAttribute("showMdBackButton", mdRepository.fileRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlBackButtonClick")
    public String HtmlSetCurrentFilesDown(Model model) 
    {
        htmlRepository.fileRepository.CurrentFilesDown();
        model.addAttribute("showHtmlBackButton", htmlRepository.fileRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/convertRepoToHtml")
    public String ConvertMdFilesToHtml(Model model) 
    {
        htmlRepository = new HTMLFileRepository();
        htmlRepository.fileRepository.SetByRepository(mdRepository, htmlRepository);
        htmlRepository.SetDefaultFilesTheme(settingsRepository.GetFileByType("styleCSS"));
        htmlRepository.AddLinksToFiles();
        model.addAttribute("showHtmlBackButton", true);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/getMdFolder")
    public String GetMdFolder(Model model)
    {
        model.addAttribute("showMdBackButton", mdRepository.fileRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/getHtmlFolder")
    public String GetHtmlFolder(Model model)
    {
        model.addAttribute("showHtmlBackButton", htmlRepository.fileRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/getHtmlPath")
    @ResponseBody
    public String GetHtmlPathByName(@RequestParam(value = "name") String name) 
    {
        return htmlRepository.GetPathByName(name);
    }

    @RequestMapping("/setSettingsFiles")
    @ResponseBody
    public void SetSettingsFiles(@RequestBody List<File> settingFiles)
    {
        settingsRepository = new FileRepository();
        settingsRepository.SetAllFiles(settingFiles);
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, value = "/p/**")
    @ResponseBody
    public String GetHtmlPage(HttpServletRequest request) {
        String restOfTheUrl = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());
        System.out.println(restOfTheUrl);
        HTMLFile htmlFile = htmlRepository.FindByPath("/" + restOfTheUrl+".html");
        if (htmlFile == null)
        {
            return "<html>\n" + "<head><title>Welcome</title></head>\n" +
          "<body>\n" + "File doesnt exist" + "\n" + "</body>\n" + "</html>";
        }
        return "<html>\n" + "<head><title>Welcome</title>"
        + "<style>" + (htmlFile.GetTheme() == null ? "" : htmlFile.GetTheme().GetText()) + "</style>" + "</head>\n" 
        + "<body>\n" + htmlFile.GetText() + "\n" + "</body>\n" + "</html>";
    }

}