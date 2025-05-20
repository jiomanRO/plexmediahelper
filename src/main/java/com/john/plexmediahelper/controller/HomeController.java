package com.john.plexmediahelper.controller;

import com.john.plexmediahelper.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class HomeController {
    @Value("${nas.media.dir.name}")
    String nasTransmissionDirName;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("transmissionFolder", nasTransmissionDirName);
        model.addAttribute("activePage", "home");
        return "index"; // Looks for templates/index.html
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("activePage", "about");
        return "about"; // Looks for templates/about.html
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("activePage", "contact");
        return "contact"; // Looks for templates/contact.html
    }
}
