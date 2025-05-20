package com.john.plexmediahelper.scheduling;

import com.john.plexmediahelper.utilities.ContentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyTaskScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyTaskScheduler.class);

    @Autowired
    ContentProcessor contentProcessor;

    //@Scheduled(cron = "0 0 10 * * *")  // Runs every day at 2:00 AM
    public void runDailyTask() {
        LOGGER.info("Executing scheduled task...");
        contentProcessor.getAndProcessContent();
        contentProcessor.deleteInvalidLinks();
        contentProcessor.createMissingLinks();
        LOGGER.info("END Executing scheduled task.");
    }
}
