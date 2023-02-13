package com.naumov.dotnetscriptsworker.service;

import com.naumov.dotnetscriptsworker.model.JobTask;

/**
 * Service for running job tasks.
 */
public interface JobService {

    /**
     * Runs the job in the blocking manner.
     *
     * @param jobTask job task to run
     */
    void runJob(JobTask jobTask);
}
