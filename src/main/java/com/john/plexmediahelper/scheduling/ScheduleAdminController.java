package com.john.plexmediahelper.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/scheduler")
public class ScheduleAdminController {

    @Autowired
    private DynamicScheduledTask dynamicTask;
    @Autowired
    ExecutionRunHistory executionRunHistory;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("currentCron", dynamicTask.getCurrentCron());
        model.addAttribute("runs", executionRunHistory.getRuns());
        model.addAttribute("activePage", "scheduler");
        return "scheduler";
    }

    @PostMapping
    public String updateCron(@RequestParam String cron, Model model) {
        dynamicTask.schedule(cron);
        model.addAttribute("message", "Updated schedule to: " + cron);
        model.addAttribute("currentCron", cron);
        model.addAttribute("activePage", "scheduler");
        return "scheduler";
    }
}
