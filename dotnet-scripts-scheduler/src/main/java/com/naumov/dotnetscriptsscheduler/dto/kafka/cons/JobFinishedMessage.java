package com.naumov.dotnetscriptsscheduler.dto.kafka.cons;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public final class JobFinishedMessage {
    @NotNull
    private UUID jobId;
    @NotNull
    private JobStatus status;
    @Valid
    private ScriptResults scriptResults;

    @Override
    public String toString() {
        return "JobFinishedMessage{" +
                "jobId='" + jobId + '\'' +
                ", status=" + status +
                ", scriptResults=" + scriptResults +
                '}';
    }
}
