package com.naumov.dotnetscriptsscheduler.exception;

public class BadInputException extends RuntimeException {
    public BadInputException(String message) {
        super(message);
    }
}
