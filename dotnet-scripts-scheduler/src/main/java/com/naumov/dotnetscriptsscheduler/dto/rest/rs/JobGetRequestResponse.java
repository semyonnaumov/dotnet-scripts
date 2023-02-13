package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobCreateRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public final class JobGetRequestResponse {
    private final UUID jobId;
    private final JobCreateRequest request;

    @Override
    public String toString() {
        return "JobGetRequestResponse{" +
                "jobId=" + jobId +
                ", request=" + request +
                '}';
    }
}
