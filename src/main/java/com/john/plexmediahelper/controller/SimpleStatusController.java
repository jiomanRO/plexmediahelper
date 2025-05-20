package com.john.plexmediahelper.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
public class SimpleStatusController {

    @RequestMapping("/status")
    public String simpleStatus() {
        return "PlexMediaHelper Application is running OK.";
    }
}
