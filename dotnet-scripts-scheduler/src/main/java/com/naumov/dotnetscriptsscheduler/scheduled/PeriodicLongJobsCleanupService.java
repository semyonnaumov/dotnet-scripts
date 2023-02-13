package com.naumov.dotnetscriptsscheduler.scheduled;

import com.naumov.dotnetscriptsscheduler.model.JobStatus;
import com.naumov.dotnetscriptsscheduler.service.JobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PeriodicLongJobsCleanupService {
    private static final Logger LOGGER = LogManager.getLogger(PeriodicLongJobsCleanupService.class);
    private final JobService jobService;
    private final int jobTimeoutMs;

    @Autowired
    public PeriodicLongJobsCleanupService(JobService jobService,
                                          @Value("${scheduler.jobs.rejected-timeout-ms}") int jobTimeoutMs) {
        this.jobService = jobService;
        this.jobTimeoutMs = jobTimeoutMs;
    }

    @Scheduled(
            initialDelayString = "${scheduler.jobs.rejected-update-period-ms}",
            fixedDelayString = "${scheduler.jobs.rejected-update-period-ms}"
    )
    public void scheduleFixedRateWithInitialDelayTask() {
        int updatedJobsNumber = jobService.rejectLongLastingJobs(jobTimeoutMs);
        if (updatedJobsNumber > 0) {
            LOGGER.info("Periodic long jobs cleanup: set {} number of jobs to {} status",
                    updatedJobsNumber, JobStatus.REJECTED);
        } else {
            LOGGER.info("Periodic long jobs cleanup: no long lasting jobs found");
        }
    }
}
