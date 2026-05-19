package com.example.service;

import com.example.domain.dto.response.JobEmailDto;
import com.example.domain.entity.Job;
import com.example.domain.entity.Skill;
import com.example.domain.entity.Subscriber;
import com.example.repository.JobRepository;
import com.example.repository.SubscriberRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmailServiceImpl implements EmailService {

    private static final Logger log = Logger.getLogger(EmailServiceImpl.class);

    private static final String EMAIL_SUBJECT = "New Jobs Matching Your Skills";
    private static final String FROM_EMAIL = "dangtruong3122005@gmail.com";
    private static final int MAX_JOBS_PER_EMAIL = 10;
    private static final String BASE_URL = "https://workhub.example.com";

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    JobRepository jobRepository;

    @Inject
    SubscriberRepository subscriberRepository;

    @Inject
    SubscriberService subscriberService;

    @Override
    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        log.info("Sending email to: " + to);

        Mail mail = Mail.withText(to, subject, content);
        mail.setFrom(FROM_EMAIL);

        try {
            reactiveMailer.send(mail)
                    .subscribe().with(
                            success -> log.info("Email sent successfully to " + to),
                            failure -> log.error("Failed to send email to " + to, failure)
                    );
        } catch (Exception e) {
            log.error("Exception while sending email to " + to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send job matching emails to all enabled subscribers
     * This is called by the scheduler
     */
    @Transactional
    public void sendJobMatchingEmails() {
        log.info("Starting job matching email job");

        List<Subscriber> activeSubscribers = subscriberService.getAllActiveEnabled();
        log.info("Found " + activeSubscribers.size() + " active subscribers");

        int successCount = 0;
        int errorCount = 0;

        for (Subscriber subscriber : activeSubscribers) {
            try {
                List<Job> matchingJobs = findMatchingJobs(subscriber.skills);

                if (!matchingJobs.isEmpty()) {
                    sendJobEmail(subscriber, matchingJobs);
                    subscriberService.updateLastSent(subscriber.id);
                    successCount++;
                    log.info("Sent job matching email to: " + subscriber.email);
                } else {
                    log.info("No matching jobs for subscriber: " + subscriber.email);
                }
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to send email to subscriber: " + subscriber.email, e);
            }
        }

        log.info("Job matching email job completed. Success: " + successCount + ", Errors: " + errorCount);
    }

    /**
     * Find jobs matching subscriber's skills
     */
    public List<Job> findMatchingJobs(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }

        // Get skill IDs
        List<Long> skillIds = skills.stream()
                .map(s -> s.id)
                .collect(Collectors.toList());

        // Find jobs with matching skills that are open and active
        Instant now = Instant.now();
        List<Job> allJobs = jobRepository.findWithFilter(
                "(endDate IS NULL OR endDate > :now) AND deleted = false",
                io.quarkus.panache.common.Parameters.with("now", now),
                io.quarkus.panache.common.Sort.descending("createdAt"),
                0, 100 // Limit to avoid too many results
        );

        // Filter jobs that have matching skills
        Set<Long> matchingSkillIds = skillIds.stream().collect(Collectors.toSet());
        List<Job> matchingJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (job.skills != null) {
                boolean hasMatch = job.skills.stream()
                        .anyMatch(s -> matchingSkillIds.contains(s.id));
                if (hasMatch) {
                    matchingJobs.add(job);
                }
            }
        }

        // Limit results
        return matchingJobs.stream()
                .limit(MAX_JOBS_PER_EMAIL)
                .collect(Collectors.toList());
    }

    /**
     * Send email to subscriber with matching jobs
     */
    private void sendJobEmail(Subscriber subscriber, List<Job> jobs) {
        String emailBody = buildEmailBody(subscriber.name, jobs);
        sendEmail(subscriber.email, EMAIL_SUBJECT, emailBody, false);
    }

    /**
     * Build email body with job listings
     */
    private String buildEmailBody(String subscriberName, List<Job> jobs) {
        StringBuilder sb = new StringBuilder();

        sb.append("Hello ").append(subscriberName).append(",\n\n");
        sb.append("We found new jobs matching your skills:\n\n");

        int index = 1;
        for (Job job : jobs) {
            sb.append(index).append(". ").append(job.name).append("\n");
            if (job.company != null) {
                sb.append("   Company: ").append(job.company.name).append("\n");
            }
            if (job.location != null) {
                sb.append("   Location: ").append(job.location).append("\n");
            }
            if (job.salary > 0) {
                sb.append("   Salary: $").append(String.format("%.0f", job.salary)).append("\n");
            }
            if (job.level != null) {
                sb.append("   Level: ").append(job.level).append("\n");
            }
            sb.append("   Apply: ").append(BASE_URL).append("/jobs/").append(job.id).append("\n");
            sb.append("\n");
            index++;
        }

        sb.append("\n---\n");
        sb.append("Best regards,\n");
        sb.append("WorkHub Team\n\n");
        sb.append("To manage your subscription, visit: ").append(BASE_URL).append("/subscribers\n");
        sb.append("To unsubscribe, reply with 'UNSUBSCRIBE' in the subject.");

        return sb.toString();
    }

    /**
     * Build HTML email body (optional, for rich formatting)
     */
    private String buildHtmlEmailBody(String subscriberName, List<Job> jobs) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html><html><body style='font-family: Arial, sans-serif;'>");
        sb.append("<h2>Hello ").append(subscriberName).append(",</h2>");
        sb.append("<p>We found <strong>").append(jobs.size()).append("</strong> new jobs matching your skills:</p>");

        sb.append("<div style='margin: 20px 0;'>");
        for (Job job : jobs) {
            sb.append("<div style='border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px;'>");
            sb.append("<h3 style='margin: 0 0 10px 0;'>").append(job.name).append("</h3>");
            if (job.company != null) {
                sb.append("<p style='margin: 5px 0;'><strong>Company:</strong> ").append(job.company.name).append("</p>");
            }
            if (job.location != null) {
                sb.append("<p style='margin: 5px 0;'><strong>Location:</strong> ").append(job.location).append("</p>");
            }
            if (job.salary > 0) {
                sb.append("<p style='margin: 5px 0;'><strong>Salary:</strong> $").append(String.format("%.0f", job.salary)).append("/month</p>");
            }
            if (job.level != null) {
                sb.append("<p style='margin: 5px 0;'><strong>Level:</strong> ").append(job.level).append("</p>");
            }
            sb.append("<a href='").append(BASE_URL).append("/jobs/").append(job.id).append("' ")
              .append("style='display: inline-block; background: #007bff; color: white; padding: 10px 20px; ")
              .append("text-decoration: none; border-radius: 5px; margin-top: 10px;'>Apply Now</a>");
            sb.append("</div>");
        }
        sb.append("</div>");

        sb.append("<hr style='margin: 20px 0;'>");
        sb.append("<p style='color: #666; font-size: 12px;'>");
        sb.append("To manage your subscription, visit: <a href='").append(BASE_URL).append("/subscribers'>My Subscriptions</a><br>");
        sb.append("To unsubscribe, reply with 'UNSUBSCRIBE' in the subject.");
        sb.append("</p></body></html>");

        return sb.toString();
    }

    /**
     * Test method: Find matching jobs for first active subscriber
     */
    public List<JobEmailDto> findMatchingJobsForTest() {
        List<Subscriber> activeSubscribers = subscriberService.getAllActiveEnabled();
        if (activeSubscribers.isEmpty()) {
            return new ArrayList<>();
        }

        Subscriber subscriber = activeSubscribers.get(0);
        List<Job> matchingJobs = findMatchingJobs(subscriber.skills);

        // Convert to DTO
        List<JobEmailDto> result = new ArrayList<>();
        for (Job job : matchingJobs) {
            JobEmailDto dto = new JobEmailDto();
            dto.jobId = job.id;
            dto.jobName = job.name;
            dto.companyName = job.company != null ? job.company.name : null;
            dto.location = job.location;
            dto.salary = job.salary;
            dto.level = job.level != null ? job.level.name() : null;
            dto.description = job.description;
            dto.startDate = job.startDate;
            dto.endDate = job.endDate;
            dto.applyUrl = BASE_URL + "/jobs/" + job.id;

            if (job.skills != null && subscriber.skills != null) {
                Set<Long> subscriberSkillIds = subscriber.skills.stream()
                        .map(s -> s.id).collect(Collectors.toSet());
                List<String> matchingSkillNames = job.skills.stream()
                        .filter(s -> subscriberSkillIds.contains(s.id))
                        .map(s -> s.name)
                        .collect(Collectors.toList());
                dto.matchingSkills = matchingSkillNames;
            }

            result.add(dto);
        }

        return result;
    }
}