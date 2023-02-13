package com.naumov.dotnetscriptsworker.service.exception;

public class ScriptFilesServiceException extends RuntimeException {

    public ScriptFilesServiceException(String message) {
        super(message);
    }

    public ScriptFilesServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
