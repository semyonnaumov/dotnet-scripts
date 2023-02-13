package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class DefaultErrorResponse {
    private final String message;

    @Override
    public String toString() {
        return "DefaultErrorResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}