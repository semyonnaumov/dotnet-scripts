package com.naumov.dotnetscriptsscheduler.service;

import java.util.Set;

public interface WorkerTypesService {

    Set<String> getAllWorkerTypes();

    boolean workerExists(String agentName);
}
