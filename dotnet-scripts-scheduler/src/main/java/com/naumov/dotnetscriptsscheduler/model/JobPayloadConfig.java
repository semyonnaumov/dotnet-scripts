package com.naumov.dotnetscriptsscheduler.model;

import lombok.*;

import java.io.Serializable;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPayloadConfig implements Serializable {
    private String nugetConfigXml;

    @Override
    public String toString() {
        return "JobPayloadConfig{" +
                "nugetConfigXml='" + omitLongString(nugetConfigXml) + '\'' +
                '}';
    }
}
