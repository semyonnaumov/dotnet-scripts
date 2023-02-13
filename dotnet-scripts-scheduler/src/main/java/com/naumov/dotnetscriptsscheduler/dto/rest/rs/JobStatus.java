package com.naumov.dotnetscriptsscheduler.dto.rest.rs;

// PENDING --+--> RUNNING ---> FINISHED
//           +--> REJECTED
public enum JobStatus {
    PENDING,
    RUNNING,
    FINISHED,
    REJECTED
}