package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public final class JobCreateResponse {
    private final UUID jobId;

    @Override
    public String toString() {
        return "JobCreateResponse{" +
                "jobId='" + jobId + '\'' +
                '}';
    }
}
