package com.naumov.dotnetscriptsscheduler.service.impl;

import com.naumov.dotnetscriptsscheduler.kafka.JobMessagesProducer;
import com.naumov.dotnetscriptsscheduler.model.*;
import com.naumov.dotnetscriptsscheduler.repository.JobsRepository;
import com.naumov.dotnetscriptsscheduler.service.JobService;
import com.naumov.dotnetscriptsscheduler.service.exception.JobServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {
    private static final Logger LOGGER = LogManager.getLogger(JobServiceImpl.class);
    private final JobsRepository jobsRepository;
    private final JobMessagesProducer jobMessagesProducer;

    @Autowired
    public JobServiceImpl(JobsRepository jobsRepository, JobMessagesProducer jobMessagesProducer) {
        this.jobsRepository = jobsRepository;
        this.jobMessagesProducer = jobMessagesProducer;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @Override
    public JobCreationResult createOrGetJob(JobRequest jobRequest) {
        LOGGER.debug("Creating job for request {}", jobRequest);
        validateJobRequest(jobRequest);

        Optional<Job> foundJobOptional = jobsRepository.findByRequestMessageId(jobRequest.getMessageId());
        if (foundJobOptional.isPresent()) {
            LOGGER.info("Skip job creation, found existing job with message id {}", jobRequest.getMessageId());
            return JobCreationResult.ofExistingJob(foundJobOptional.get());
        }

        try {
            Job newJob = prepareJob(jobRequest);
            Job savedJob = jobsRepository.saveAndFlush(newJob); // all associations saved here using cascade
            jobMessagesProducer.sendJobTaskMessageAsync(savedJob);
            LOGGER.info("Created new job {}", savedJob.getId());

            return JobCreationResult.ofNewJob(savedJob);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to create new job from job request {}", jobRequest, e);
            throw e;
        }
    }

    private void validateJobRequest(JobRequest jobRequest) {
        if (jobRequest == null) throw new JobServiceException("Cannot create job from null request");
        if (jobRequest.getMessageId() == null) throw new JobServiceException("No message id in job request");
        if (jobRequest.getId() != null) throw new JobServiceException("Job creation request cannot have an ID");
    }

    private Job prepareJob(JobRequest jobRequest) {
        return Job.builder()
                .status(JobStatus.PENDING)
                .request(jobRequest)
                .build();
    }

    @Transactional
    @Override
    public Optional<Job> findJob(UUID id) {
        return jobsRepository.findById(id);
    }

    @Transactional
    @Override
    public Optional<JobRequest> findJobRequestByJobId(UUID id) {
        return jobsRepository.findJobRequestByJobId(id);
    }

    @Transactional
    @Override
    public Optional<JobStatus> findJobStatusByJobId(UUID id) {
        return jobsRepository.findJobStatusByJobId(id);
    }

    @Transactional
    @Override
    public Optional<JobResult> findJobResultByJobId(UUID id) {
        return jobsRepository.findJobResultByJobId(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @Override
    public boolean deleteJob(UUID id) {
        LOGGER.debug("Deleting job {}", id);

        boolean jobExists = jobsRepository.existsById(id);
        if (!jobExists) {
            LOGGER.info("Job {} requested for deletion not found", id);
            return false;
        }

        try {
            jobsRepository.deleteById(id);
            LOGGER.info("Deleted job {}", id);

            return true;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to delete job {}", id, e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void updateStartedJob(UUID id) {
        LOGGER.debug("Updating started job {}", id);

        JobStatus runningStatus = JobStatus.RUNNING;
        try {
            Optional<JobStatus> jobStatusOptional = jobsRepository.findJobStatusByJobId(id);
            if (jobStatusOptional.isEmpty()) {
                LOGGER.warn("Unable to update started job {}: job not found", id);
                return;
            }

            JobStatus currentStatus = jobStatusOptional.get();
            if (currentStatus == JobStatus.PENDING) {
                jobsRepository.updateJobSetStatusTo(id, runningStatus);
                LOGGER.info("Set job {} status to {}", id, runningStatus);
            } else {
                // RUNNING, FINISHED or REJECTED
                LOGGER.warn("Unable to set job {} status to {}: current status is {}",
                        id, runningStatus, currentStatus);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to update started job {}", id, e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void updateFinishedJob(Job job) {
        UUID jobId = job.getId();
        LOGGER.debug("Updating finished job {}", jobId);

        try {
            Optional<Job> savedJobOptional = jobsRepository.findById(jobId);
            if (savedJobOptional.isEmpty()) {
                LOGGER.warn("Unable to update finished job {}: job not found", jobId);
                return;
            }

            Job savedJob = savedJobOptional.get();
            JobStatus currentStatus = savedJob.getStatus();
            if (currentStatus == JobStatus.PENDING || currentStatus == JobStatus.RUNNING) {
                savedJob.setStatus(job.getStatus());
                savedJob.setResult(job.getResult());
                jobsRepository.save(savedJob);

                LOGGER.info("Successfully updated finished job {}", jobId);
            } else {
                // FINISHED or REJECTED
                LOGGER.warn("Unable to set job {} result: current status is {}", jobId, currentStatus);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to update finished job {}", jobId, e);
            throw e;
        }
    }

    @Transactional
    @Override
    public int rejectLongLastingJobs(int jobTimeoutMs) {
        return jobsRepository.rejectJobsRunningLongerThanJobTimeout(Duration.ofMillis(jobTimeoutMs));
    }
}
