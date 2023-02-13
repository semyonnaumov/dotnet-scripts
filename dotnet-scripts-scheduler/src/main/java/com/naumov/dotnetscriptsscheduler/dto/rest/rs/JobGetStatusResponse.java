package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public final class JobGetStatusResponse {
    private final UUID jobId;
    private final JobStatus status;

    @Override
    public String toString() {
        return "JobGetStatusResponse{" +
                "jobId=" + jobId +
                ", status=" + status +
                '}';
    }
}
