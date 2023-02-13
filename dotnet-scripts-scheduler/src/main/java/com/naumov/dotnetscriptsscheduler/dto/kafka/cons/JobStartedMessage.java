package com.naumov.dotnetscriptsscheduler.dto.kafka.cons;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public final class JobStartedMessage {
    @NotNull
    private UUID jobId;

    @Override
    public String toString() {
        return "JobStartedMessage{" +
                "jobId='" + jobId + '\'' +
                '}';
    }
}
