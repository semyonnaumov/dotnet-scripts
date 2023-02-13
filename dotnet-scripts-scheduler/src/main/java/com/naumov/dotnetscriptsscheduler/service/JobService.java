package com.naumov.dotnetscriptsscheduler.service;

import com.naumov.dotnetscriptsscheduler.model.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Service, responsible for jobs manipulation.
 */
public interface JobService {

    /**
     * Creates new job from {@code jobRequest} or finds existing job with the same {@code jobRequest.messageId}.
     * Returned {@link JobCreationResult} contains the flag indicating which one of above-mentioned happened.
     * This operation is idempotent at the scope of {@code JobCreationResult.job}.
     *
     * @param jobRequest request to create the job from
     * @return the job and "created" flag wrapper
     */
    JobCreationResult createOrGetJob(JobRequest jobRequest);

    /**
     * Finds job by its id.
     *
     * @param id job to find
     * @return result of job search
     */
    Optional<Job> findJob(UUID id);

    /**
     * Finds job request by job id.
     *
     * @param id job to find request for
     * @return result of job request search
     */
    Optional<JobRequest> findJobRequestByJobId(UUID id);

    /**
     * Finds job status by job id.
     *
     * @param id job to find status of
     * @return result of job status search
     */
    Optional<JobStatus> findJobStatusByJobId(UUID id);

    /**
     * Finds job result by job id.
     *
     * @param id job to find result for
     * @return result of job result search
     */
    Optional<JobResult> findJobResultByJobId(UUID id);

    /**
     * Deletes job by id. Returns {@code true} if job deleted, {@code false} if not found.
     *
     * @param id to delete job for
     * @return "success" flag of deletion
     */
    boolean deleteJob(UUID id);

    /**
     * Updates status of started job. Sets the status to RUNNING if the job's status is PENDING.
     * If the job's status is in (RUNNING, FINISHED, REJECTED) does nothing.
     * This operation is idempotent.
     *
     * @param id job id to update status for
     */
    void updateStartedJob(UUID id);

    /**
     * Updates status of finished job. Sets the status to FINISHED or REJECTED depending
     * on {@code job.status} if its' status is either PENDING or RUNNING.
     * If the job's status is in (FINISHED, REJECTED) does nothing. If job's status is updated,
     * sets the result of the job from {@code job.result}.
     * This operation is idempotent.
     *
     * @param job job to update status and set result for
     */
    void updateFinishedJob(Job job);

    /**
     * Updates statuses of jobs, running longer than jobTimeoutMs. Sets job status
     * to REJECTED if its status is in (PENDING, RUNNING). If the job's status is in
     * (FINISHED, REJECTED) does nothing. If the job's status is updated,
     * the {@code job.result} is cleared.
     * This operation is idempotent.
     *
     * @param jobTimeoutMs running job validity period
     * @return the number of updated jobs
     */
    int rejectLongLastingJobs(int jobTimeoutMs);
}
