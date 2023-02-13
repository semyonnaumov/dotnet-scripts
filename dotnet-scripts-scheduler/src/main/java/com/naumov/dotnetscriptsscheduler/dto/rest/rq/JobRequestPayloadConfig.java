package com.naumov.dotnetscriptsscheduler.dto.rest.rq;

import lombok.*;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class JobRequestPayloadConfig {
    private String nugetConfigXml;

    @Override
    public String toString() {
        return "JobRequestPayloadConfig{" +
                "nugetConfigXml='" + omitLongString(nugetConfigXml) + '\'' +
                '}';
    }
}