package com.naumov.dotnetscriptsscheduler.model;

import lombok.Getter;

@Getter
public final class JobCreationResult {
    private final Job job;
    private final boolean isCreated;

    private JobCreationResult(Job job, boolean isCreated) {
        this.job = job;
        this.isCreated = isCreated;
    }

    public static JobCreationResult ofNewJob(Job job) {
        return new JobCreationResult(job, true);
    }

    public static JobCreationResult ofExistingJob(Job job) {
        return new JobCreationResult(job, false);
    }
}
