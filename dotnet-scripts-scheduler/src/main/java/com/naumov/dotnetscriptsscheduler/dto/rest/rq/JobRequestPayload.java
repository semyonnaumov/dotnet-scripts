package com.naumov.dotnetscriptsscheduler.dto.rest.rq;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class JobRequestPayload {
    @NotNull
    private String script;
    @Valid
    private JobRequestPayloadConfig jobConfig;
    @NotBlank
    private String agentType;

    @Override
    public String toString() {
        return "JobRequestPayload{" +
                "script='" + omitLongString(script) + '\'' +
                ", jobConfig=" + jobConfig +
                ", agentType='" + agentType + '\'' +
                '}';
    }
}