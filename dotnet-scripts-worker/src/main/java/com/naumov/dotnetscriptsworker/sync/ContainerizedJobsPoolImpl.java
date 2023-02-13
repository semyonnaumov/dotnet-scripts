package com.naumov.dotnetscriptsworker.sync;

import com.naumov.dotnetscriptsworker.config.props.SandboxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
public final class ContainerizedJobsPoolImpl implements ContainerizedJobsPool {
    private final int maxCapacity;
    private final Map<UUID, ContainerizedJob> containerizedJobsMap = new HashMap<>();
    private final ReentrantLock containerizedJobsMapLock = new ReentrantLock();
    private final Condition containerSlotAvailableCondition = containerizedJobsMapLock.newCondition();

    @Autowired
    public ContainerizedJobsPoolImpl(SandboxProperties sandboxProperties) {
        this.maxCapacity = sandboxProperties.getMaxContainers();
    }

    @Override
    public ContainerizedJob tryAllocate(UUID jobId, long timeoutMs) throws ContainerizedJobAllocationException {
        long timeRemainingNs = TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        containerizedJobsMapLock.lock();
        try {
            if (containerizedJobsMap.containsKey(jobId)) {
                ContainerizedJob containerizedJob = containerizedJobsMap.get(jobId);
                containerizedJob.secondAllocationAttempted();
                return containerizedJob;
            }

            while (containerizedJobsMap.size() >= maxCapacity) {
                if (timeRemainingNs <= 0L) {
                    throw new ContainerizedJobAllocationException("Failed to allocate new containerized job for job "
                            + jobId + ", timeout exceeded");
                }
                timeRemainingNs = containerSlotAvailableCondition.awaitNanos(timeRemainingNs);
            }

            ContainerizedJob containerizedJob = new ContainerizedJob(jobId);
            containerizedJobsMap.put(jobId, containerizedJob);

            return containerizedJob;
        } catch (InterruptedException e) {
            throw new ContainerizedJobAllocationException("Failed to allocate new containerized job for job "
                    + jobId + ", thread interrupted", e);
        } finally {
            containerizedJobsMapLock.unlock();
        }
    }

    @Override
    public void reclaim(ContainerizedJob containerizedJob) {
        Objects.requireNonNull(containerizedJob, "Parameter containerizedJob must not be null");
        containerizedJobsMapLock.lock();
        try {
            UUID key = containerizedJob.getJobId();
            ContainerizedJob foundContainerizedJob = containerizedJobsMap.get(key);
            if (containerizedJob.equals(foundContainerizedJob)) {
                containerizedJobsMap.remove(key);
                containerizedJob.reclaim();
                containerSlotAvailableCondition.signalAll();
            }
        } finally {
            containerizedJobsMapLock.unlock();
        }
    }

    @Override
    public List<ContainerizedJob> getContainerizedJobs() {
        containerizedJobsMapLock.lock();
        try {
            return containerizedJobsMap.values().stream().toList();
        } finally {
            containerizedJobsMapLock.unlock();
        }
    }
}
