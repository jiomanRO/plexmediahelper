package com.john.plexmediahelper.controller;

import com.john.plexmediahelper.scheduling.ExecutionRunHistory;
import com.john.plexmediahelper.scheduling.model.ScheduledExecutionRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/runs")
public class RunController {

    @Autowired
    ExecutionRunHistory executionRunHistory;

    @GetMapping("/{id}")
    public String showRunDetails(@PathVariable Long id, Model model) {
        ScheduledExecutionRun selectedRun = null;
        for(ScheduledExecutionRun run : executionRunHistory.getRuns()) {
            if(run.getID() == id.intValue())
                selectedRun = run;
        }
        if (selectedRun == null) {
            // handle not found, maybe throw 404 or redirect
            return "redirect:/scheduler";
        }
        model.addAttribute("run", selectedRun);
        return "runDetails";  // Thymeleaf template runDetails.html
    }
}
