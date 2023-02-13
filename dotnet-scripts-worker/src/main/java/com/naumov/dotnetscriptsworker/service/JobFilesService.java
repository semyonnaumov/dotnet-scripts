package com.naumov.dotnetscriptsworker.service;

import com.naumov.dotnetscriptsworker.model.JobTask;

import java.util.UUID;

public interface JobFilesService {

    String prepareJobFiles(JobTask jobTask);

    void cleanupJobFiles(UUID jobId);
}
