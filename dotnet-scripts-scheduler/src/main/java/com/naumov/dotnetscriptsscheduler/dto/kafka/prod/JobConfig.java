package com.naumov.dotnetscriptsscheduler.dto.kafka.prod;

import lombok.Builder;
import lombok.Getter;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Builder
public final class JobConfig {
    private final String nugetConfigXml;

    @Override
    public String toString() {
        return "JobConfig{" +
                "nugetConfigXml='" + omitLongString(nugetConfigXml) + '\'' +
                '}';
    }
}