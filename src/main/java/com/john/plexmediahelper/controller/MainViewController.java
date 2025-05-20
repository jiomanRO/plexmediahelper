package com.john.plexmediahelper.controller;

import com.john.plexmediahelper.model.Data;
import com.john.plexmediahelper.model.Item;
import com.john.plexmediahelper.utilities.ContentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainViewController {

    @Value("${nas.media.dir.name}")
    String NASFolder;

    @Autowired
    ContentProcessor contentProcessor;
    @Autowired
    Data data;

    @GetMapping("/mainView")
    public String mainView(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        contentProcessor.getAndProcessContent();
        model.addAttribute("NASFolder", NASFolder);
        model.addAttribute("items", data.getAllItemsList());
        model.addAttribute("totalItems", data.getAllItemsList().size());
        Map<String, Long> countsByType = data.getAllItemsList().stream()
                .collect(Collectors.groupingBy(Item::getContentType, Collectors.counting()));
        model.addAttribute("movieItems", countsByType.getOrDefault("Movie", 0L));
        model.addAttribute("tvShowItems", countsByType.getOrDefault("TVShow", 0L));
        model.addAttribute("otherItems", countsByType.getOrDefault("Other", 0L));
        model.addAttribute("moviesLinksCount", data.getCurrentMoviesLinks().size());
        model.addAttribute("tvShowsLinksCount", data.getCurrentTVShowsLinks().size());
        Map<String, Long> countsByStatus = data.getAllItemsList().stream()
                .collect(Collectors.groupingBy(Item::getStatus, Collectors.counting()));
        model.addAttribute("missingLinksCount",countsByStatus.getOrDefault("Missing", 0L));
        model.addAttribute("invalidLinksCount",data.getInvalidLinks().size());
        return "mainView"; // Looks for templates/mainView.html
    }

    @PostMapping("/createLinks")
    public String createLinks(Model model) {
        contentProcessor.createLinks();
        return "redirect:/";
    }

    @PostMapping("/createMissingLinks")
    public String createMissingLinks(Model model) {
        contentProcessor.createMissingLinks();
        return "redirect:/";
    }

    @PostMapping("/deleteAllLinks")
    public String deleteAllLinks(Model model) {
        contentProcessor.deleteAllLinks();
        return "redirect:/";
    }

    @PostMapping("/deleteInvalidLinks")
    public String deleteInvalidLinks(Model model) {
        contentProcessor.deleteInvalidLinks();
        return "redirect:/";
    }
}
