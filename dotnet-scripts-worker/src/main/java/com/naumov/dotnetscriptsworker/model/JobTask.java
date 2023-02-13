package com.naumov.dotnetscriptsworker.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public final class JobTask {
    private final UUID jobId;
    private final UUID messageId;
    private final String jobScript;
    private final JobConfig jobConfig;

    @Getter
    @Builder
    public static final class JobConfig {
        private final String nugetConfigXml;
    }
}
