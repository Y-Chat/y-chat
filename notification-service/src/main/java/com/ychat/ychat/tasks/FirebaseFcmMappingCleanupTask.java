package com.ychat.ychat.tasks;

import com.ychat.ychat.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FirebaseFcmMappingCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseFcmMappingCleanupTask.class);

    private final NotificationService notificationService;

    public FirebaseFcmMappingCleanupTask(@Autowired NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 24)
    public void run() {
        logger.info("Starting FirebaseFcmMappingCleanupTask");
        notificationService.cleanupStaleTokens();
        logger.info("Finished FirebaseFcmMappingCleanupTask");
    }
}
