package com.example.SimpleWiki.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.SimpleWiki.model.MDFile;
import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.repository.HTMLFileRepository;
import com.example.SimpleWiki.repository.MDFileRepository;

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
        return mdFile.ConvertToHtml(mdFile.text);
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
        mdRepository.fileRepository.SetFolderPath(currentFolder);
        htmlRepository.fileRepository.SetFolderPath(currentFolder);
        mdRepository.fileRepository.SetClearRepository();
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
        htmlRepository.fileRepository.SetClearRepository();
        htmlRepository.fileRepository.SetByRepository(mdRepository.fileRepository, htmlRepository, complexFiles);
        model.addAttribute("showHtmlBackButton", true);
        model.addAttribute("currentHtmlFiles", htmlRepository.fileRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }
}