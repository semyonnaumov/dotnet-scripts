package com.naumov.dotnetscriptsworker.service;

import com.naumov.dotnetscriptsworker.model.JobTask;

import java.util.UUID;

/**
 * Service to prepare and cleanup job temp files.
 */
public interface JobFilesService {

    /**
     * Creates temp folder and files necessary for running the {@code jobTask}.
     *
     * @param jobTask job task to create files for
     * @return path to temp directory with job task files
     */
    String prepareJobFiles(JobTask jobTask);

    /**
     * Removes the temp directory with the temp files for the job {@code jobId}
     *
     * @param jobId job to perform cleanup for
     */
    void cleanupJobFiles(UUID jobId);
}
