package com.naumov.dotnetscriptsworker.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public final class JobResults {
    private final UUID jobId;
    private final Status status;
    private final ScriptResults scriptResults;

    public enum Status {
        ACCEPTED,
        REJECTED
    }

    @Getter
    @Builder
    public static final class ScriptResults {
        private final JobCompletionStatus finishedWith;
        private final String stdout;
        private final String stderr;

        public enum JobCompletionStatus {
            SUCCEEDED,
            FAILED,
            TIME_LIMIT_EXCEEDED
        }
    }
}
