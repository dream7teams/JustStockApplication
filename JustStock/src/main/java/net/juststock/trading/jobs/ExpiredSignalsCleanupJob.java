package net.juststock.trading.jobs;

import net.juststock.trading.service.ExpiredSignalsCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredSignalsCleanupJob {

    private final ExpiredSignalsCleanupService cleanupService;

    public ExpiredSignalsCleanupJob(ExpiredSignalsCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    // Run daily at 03:15 AM server time (adjust as needed)
    @Scheduled(cron = "0 15 3 * * *")
    public void runNightlyCleanup() {
        cleanupService.purgeExpiredMessages();
    }
}
