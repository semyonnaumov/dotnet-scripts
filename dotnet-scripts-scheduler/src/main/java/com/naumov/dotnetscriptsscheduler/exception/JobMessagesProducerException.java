package com.naumov.dotnetscriptsscheduler.exception;

public class JobMessagesProducerException extends RuntimeException {
    public JobMessagesProducerException(String message) {
        super(message);
    }

    public JobMessagesProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}
