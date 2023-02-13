package com.naumov.dotnetscriptsscheduler.dto.kafka.prod;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@Builder
public final class JobTaskMessage {
    private UUID jobId;
    private String script;
    private JobConfig jobConfig;

    @Override
    public String toString() {
        return "JobTaskMessage{" +
                "jobId='" + jobId + '\'' +
                ", script='" + omitLongString(script) + '\'' +
                ", jobConfig=" + jobConfig +
                '}';
    }
}
