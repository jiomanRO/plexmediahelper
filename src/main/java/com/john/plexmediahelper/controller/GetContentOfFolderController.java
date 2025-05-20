package com.john.plexmediahelper.controller;

import com.john.plexmediahelper.services.SSHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetContentOfFolderController {

    @Autowired
    SSHService sshService;
    @Value("${nas.media.dir.name}")
    String folder;

    @RequestMapping("/content")
    public List<String> getContent() {
        return sshService.getContentOfDir(folder);
    }
}
