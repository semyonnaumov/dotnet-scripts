package com.naumov.dotnetscriptsworker.sync;

public class ContainerizedJobAllocationException extends RuntimeException {

    public ContainerizedJobAllocationException(String message) {
        super(message);
    }

    public ContainerizedJobAllocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
