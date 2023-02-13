package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public final class JobGetResultResponse {
    private final UUID jobId;
    private final JobResult result;

    @Override
    public String toString() {
        return "JobGetResultResponse{" +
                "jobId=" + jobId +
                ", result=" + result +
                '}';
    }
}
