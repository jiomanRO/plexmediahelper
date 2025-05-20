package com.john.plexmediahelper.scheduling;

import com.john.plexmediahelper.model.Data;
import com.john.plexmediahelper.model.Item;
import com.john.plexmediahelper.scheduling.model.ScheduledExecutionRun;
import com.john.plexmediahelper.utilities.ContentProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component
public class DynamicScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduledTask.class);
    @Value("${nas.media.dir.name}")
    String NASFolder;
    @Value("${app.scheduling.cron}")
    private String defaultCron;
    private final ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture<?> scheduledFuture;
    private String currentCron;

    @Value("${scheduling.timezone}")
    private String timezone;
    @Autowired
    ContentProcessor contentProcessor;
    @Autowired
    ExecutionRunHistory executionRunHistory;
    @Autowired
    Data data;

    @Autowired
    public DynamicScheduledTask(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void scheduleFromProperties() {
        schedule(defaultCron);
    }

    private final Runnable task = () -> {

        //keep only the latest 100 runs
        if(executionRunHistory.getRuns().size() > 100) {
            executionRunHistory.getRuns().remove(0);
        }

        ScheduledExecutionRun run = new ScheduledExecutionRun();
        run.setID(executionRunHistory.getRuns().size()+1);
        //start
        Date startDate = new java.util.Date();
        LOGGER.info("Running scheduled task at " + startDate);
        run.setStartDate(startDate);
        //get content
        contentProcessor.getAndProcessContent();
        run.setNasTransmissionDirName(NASFolder);
        run.setTotalItemsCount(data.getAllItemsList().size());
        Map<String, Long> countsByType = data.getAllItemsList().stream()
                .collect(Collectors.groupingBy(Item::getContentType, Collectors.counting()));
        run.setMoviesCount(countsByType.getOrDefault("Movie", 0L).intValue());
        run.setTvShowsCount(countsByType.getOrDefault("TVShow", 0L).intValue());
        run.setOtherCount(countsByType.getOrDefault("Other", 0L).intValue());
        run.setMoviesLinksCountBefore(data.getCurrentMoviesLinks().size());
        run.setTvShowsLinksCountBefore(data.getCurrentTVShowsLinks().size());
        Map<String, Long> countsByStatus = data.getAllItemsList().stream()
                .collect(Collectors.groupingBy(Item::getStatus, Collectors.counting()));
        run.setMissingLinksCountBefore(countsByStatus.getOrDefault("Missing", 0L).intValue());
        run.setInvalidLinksCountBefore(data.getInvalidLinks().size());
        //delete invalid links
        contentProcessor.deleteInvalidLinks();
        //create missing links
        contentProcessor.createMissingLinks();
        //get content
        contentProcessor.getAndProcessContent();
        run.setMoviesLinksCountAfter(data.getCurrentMoviesLinks().size());
        run.setTvShowsLinksCountAfter(data.getCurrentTVShowsLinks().size());
        countsByStatus = data.getAllItemsList().stream()
                .collect(Collectors.groupingBy(Item::getStatus, Collectors.counting()));
        run.setMissingLinksCountAfter(countsByStatus.getOrDefault("Missing", 0L).intValue());
        run.setInvalidLinksCountAfter(data.getInvalidLinks().size());
        //end
        Date endDate = new java.util.Date();
        LOGGER.info("END executing scheduled task at " + endDate);
        run.setEndDate(endDate);
        String detail = new String("");
        if(run.getInvalidLinksCountBefore() != run.getInvalidLinksCountAfter()) {
            detail += "Deleted " +  (run.getInvalidLinksCountBefore() - run.getInvalidLinksCountAfter()) + " invalid link(s).";
        }
        if(run.getMissingLinksCountBefore() != run.getMissingLinksCountAfter()) {
            if(!detail.equals(""))
                detail += " ";
            detail += "Created " + (run.getMissingLinksCountBefore() - run.getMissingLinksCountAfter()) + " missing link(s).";
        }
        if(!detail.equals(""))
            run.setDetail(detail);
        else
            run.setDetail("Nothing to delete/create.");
        if(run.getMissingLinksCountAfter() == 0 && run.getInvalidLinksCountAfter() == 0) {
            run.setRunStatus("Success");
        } else {
            run.setRunStatus("Unsuccessful");
        }
        executionRunHistory.getRuns().add(run);
    };

    public synchronized void schedule(String cron) {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = scheduler.schedule(task, new CronTrigger(cron, ZoneId.of(timezone)));
        currentCron = cron;
        LOGGER.info("Scheduled task with cron: " + cron + " in timezone: " + ZoneId.of(timezone));
    }

    public synchronized String getCurrentCron() {
        return currentCron;
    }

    public synchronized void cancel() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
    }
}
