package com.naumov.dotnetscriptsworker.sync;

import java.util.Objects;
import java.util.UUID;

/**
 * Containerized job for {@link ContainerizedJobsPool}.
 * Equality is based on reference equality (instance equality).
 */
public final class ContainerizedJob {
    private final UUID jobId;
    private volatile String containerId;
    private volatile boolean requestedMultipleTimes = false; // for docker-wide deduping purposes
    private volatile boolean reclaimed = false;

    public ContainerizedJob(UUID jobId) {
        Objects.requireNonNull(jobId, "Parameter jobId must not be null");
        this.jobId = jobId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public boolean isRequestedMultipleTimes() {
        return requestedMultipleTimes;
    }

    public boolean isReclaimed() {
        return reclaimed;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    void secondAllocationAttempted() {
        this.requestedMultipleTimes = true;
    }

    void reclaim() {
        this.reclaimed = true;
    }

    @Override
    public String toString() {
        return "ContainerizedJob{" +
                "jobId='" + jobId + '\'' +
                ", containerId='" + containerId + '\'' +
                ", requestedMultipleTimes=" + requestedMultipleTimes +
                ", reclaimed=" + reclaimed +
                '}';
    }
}
