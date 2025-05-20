package com.john.plexmediahelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlexmediahelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlexmediahelperApplication.class, args);
    }

}
