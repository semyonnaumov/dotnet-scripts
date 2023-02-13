package com.naumov.dotnetscriptsworker.dto.prod;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public final class JobStartedMessage {
    @NotNull
    private final UUID jobId;
}
