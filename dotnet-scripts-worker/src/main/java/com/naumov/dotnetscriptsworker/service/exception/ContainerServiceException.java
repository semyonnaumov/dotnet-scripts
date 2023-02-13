package com.naumov.dotnetscriptsworker.service.exception;

public class ContainerServiceException extends RuntimeException {

    public ContainerServiceException(String message) {
        super(message);
    }

    public ContainerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
