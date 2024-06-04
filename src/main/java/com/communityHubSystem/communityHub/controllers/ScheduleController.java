package com.communityHubSystem.communityHub.controllers;
import com.communityHubSystem.communityHub.services.ScheduledUploadService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class ScheduleController {
    @Value("${upload.interval}")
    private long interval;

    private final TaskScheduler taskScheduler;
    private final ScheduledUploadService scheduledUploadService;
    private Runnable scheduledTask;

    public ScheduleController(ScheduledUploadService scheduledUploadService) {
        this.scheduledUploadService = scheduledUploadService;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    @PostMapping("/set-interval")
    public ResponseEntity<?> setInterval(@RequestParam long hours, @RequestParam long minutes) {
        long newInterval = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
        this.interval = newInterval;
        scheduleTaskWithFixedRate(newInterval);
        System.out.println("new time is "+newInterval);
        return ResponseEntity.ok("Upload interval set to " + newInterval + " milliseconds");
    }

    @GetMapping("/current-interval")
    public ResponseEntity<Long> getCurrentInterval() {
        return ResponseEntity.ok(interval);
    }

 /*   @PostConstruct
    public void init() {
        scheduleTaskWithFixedRate(interval);
    }
*/
    public void scheduleTaskWithFixedRate(long newInterval) {
        if (scheduledTask != null) {
            ((ThreadPoolTaskScheduler) taskScheduler).shutdown();
            ((ThreadPoolTaskScheduler) taskScheduler).initialize();
        }
        scheduledTask = () -> {
            try {
                scheduledUploadService.scheduleFixedRateTask();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        PeriodicTrigger trigger = new PeriodicTrigger(newInterval, TimeUnit.MILLISECONDS);
        taskScheduler.schedule(scheduledTask, trigger);
    }
}