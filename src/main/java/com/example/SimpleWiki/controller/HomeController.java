package com.example.SimpleWiki.controller;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import com.split.ftp.FtpOperation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;


import com.example.SimpleWiki.model.FileObject;
import com.example.SimpleWiki.repository.FileRepository;
import com.split.ftp.FtpOperation;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    FileRepository mdRepository;
    FileRepository htmlRepository;
    FileRepository settingsRepository;

    public HomeController()
    {
        mdRepository = new FileRepository();
        htmlRepository = new FileRepository();
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
        mdRepository.CurrentFilesUp(name);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        model.addAttribute("showMdBackButton", false);
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlDirButtonClick")
    public String HtmlDirClick(Model model, @RequestParam(value = "name") String name)
    {
        htmlRepository.CurrentFilesUp(name);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        model.addAttribute("showHtmlBackButton", false);
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/setListOfMdFiles")
    public String SetCurrentFiles(Model model, @RequestBody List<FileObject> allFiles) 
    {
        mdRepository = new FileRepository();
        htmlRepository = new FileRepository();

        mdRepository.SetAllFiles(allFiles);
        mdRepository.SetCurrentFiles();
        model.addAttribute("showMdBackButton", true);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    } 

    @RequestMapping("/mdBackButtonClick")
    public String MdSetCurrentFilesDown(Model model) 
    {
        mdRepository.CurrentFilesDown();
        model.addAttribute("showMdBackButton", mdRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlBackButtonClick")
    public String HtmlSetCurrentFilesDown(Model model) 
    {
        htmlRepository.CurrentFilesDown();
        model.addAttribute("showHtmlBackButton", htmlRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/convertRepoToHtml")
    public String ConvertMdFilesToHtml(Model model) 
    {
        htmlRepository = new FileRepository();
        htmlRepository.SetHtmlRepositoryByMd(mdRepository, settingsRepository.GetFileByType("settings"));
        model.addAttribute("showHtmlBackButton", true);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/getMdFolder")
    public String GetMdFolder(Model model)
    {
        model.addAttribute("showMdBackButton", mdRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/getHtmlFolder")
    public String GetHtmlFolder(Model model)
    {
        model.addAttribute("showHtmlBackButton", htmlRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
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
    public void SetSettingsFiles(@RequestBody List<FileObject> settingFiles)
    {
        settingsRepository = new FileRepository();
        settingsRepository.SetAllFiles(settingFiles);
    }

    @RequestMapping(value = "/f/**")
    public String GetHtmlPageFrame(HttpServletRequest request, Model model) {
        String restOfTheUrl = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());
        model.addAttribute("path", "/p/"+restOfTheUrl);
        return "fragments/pageFrame";
    }

    @RequestMapping(value = "/defaultPageFrame")
    public String GetDefaultHtmlPageFrame(Model model) {
        model.addAttribute("path", "/defaultPage");
        return "fragments/pageFrame";
    }

    @RequestMapping(value = "/defaultPage")
    public String GetDefaultHtmlPage(Model model) {
        model.addAttribute("mainTags", "<p class=\"text-info\">Build the site or select site content</p>");
        return "page";
    }

    @RequestMapping(value = "/p/**")
    public String GetHtmlPage(HttpServletRequest request, Model model) {
        String restOfTheUrl = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());
        FileObject htmlFile = htmlRepository.GetFileByPath("/" + restOfTheUrl + ".html");
        String title = "Error";
        String stylesFile = (settingsRepository.GetFileByType("theme") == null ? "" : settingsRepository.GetFileByType("theme").GetText());
        String tags = "<p>File doesnt exist</p>";
        if (htmlFile != null)
        {
            title = htmlFile.GetName();
            tags = htmlFile.GetText();
        }
        else if (restOfTheUrl.equals("Welcome"))
        {
            htmlFile = htmlRepository.GetFileByType("file");
            title = htmlFile.GetName();
            tags = htmlFile.GetText();
        }
        model.addAttribute("title", title);
        model.addAttribute("styles", stylesFile);
        model.addAttribute("mainTags", tags);
        return "page";
    }

    @RequestMapping("/uploadToFtp")
    @ResponseBody
    public String uploadToFtp(@RequestParam(name="login") String login, @RequestParam(name="password") String password, 
    @RequestParam(name="ip") String ip, @RequestParam(name="port") String port, @RequestParam(name="folder") String folder) {
        String stylesFile = (settingsRepository.GetFileByType("theme") == null ? "" : settingsRepository.GetFileByType("theme").GetText());
        String addStyles = (settingsRepository.GetFileByType("addTheme") == null ? "" : settingsRepository.GetFileByType("addTheme").GetText());
        try 
        {
            FtpOperation ftpOperation = new FtpOperation(login, password, ip, Integer.parseInt(port), "/" + folder);
            for (FileObject file: htmlRepository.GetAllFiles())
            {
                if (file.GetType().equals("dir")) 
                {
                    ftpOperation.createDirectory(file.GetPath());
                }
            }
            try 
            {
                for (FileObject file: htmlRepository.GetAllFiles())
                {
                    if (file.GetType().equals("file"))
                    {
                        Path tempFile = Files.createTempFile(null, null);
                        List<String> content = Arrays.asList(file.GetFullHtml(stylesFile, addStyles));
                        Files.write(tempFile, content, StandardOpenOption.APPEND);
                        InputStream inputStream = FileUtil.getInputStream(tempFile);
                        ftpOperation.uploadToFtp(inputStream, file.GetPath().split("\\.")[0], false);
                    }
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return "SUCCESS";
    }

}