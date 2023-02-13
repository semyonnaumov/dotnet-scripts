package com.naumov.dotnetscriptsscheduler.service.impl;

import com.naumov.dotnetscriptsscheduler.service.WorkerTypesService;

import java.util.Collections;
import java.util.Set;

/**
 * Simple in-memory worker types service implementation.
 */
public class WorkerTypesServiceImpl implements WorkerTypesService {
    private final Set<String> workerTypes;

    public WorkerTypesServiceImpl(Set<String> workerTypes) {
        this.workerTypes = Collections.unmodifiableSet(workerTypes);
    }

    @Override
    public boolean workerExists(String workerType) {
        return workerTypes.contains(workerType);
    }

    @Override
    public Set<String> getAllWorkerTypes() {
        return workerTypes;
    }
}
