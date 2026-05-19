package com.example.scheduler;

import com.example.service.EmailServiceImpl;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class JobMatchingScheduler {

    private static final Logger log = Logger.getLogger(JobMatchingScheduler.class);

    @Inject
    EmailServiceImpl emailService;

    @Scheduled(cron = "0 */30 * * * ?")
    void sendJobMatchingEmails() {
        log.info("Job Matching Scheduler triggered");
        try {
            emailService.sendJobMatchingEmails();
        } catch (Exception e) {
            log.error("Error in job matching scheduler", e);
        }
    }
}