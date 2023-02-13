package com.naumov.dotnetscriptsworker.sync;

import java.util.List;
import java.util.UUID;

/**
 * Component, used to synchronize job running threads on running containers.
 */
public interface ContainerizedJobsPool {

    /**
     * Tries to create a new entry in the containerized job pool for {@code timeoutMs}.
     * If failed to allocate during {@code timeoutMs}, throws an exception.
     * If there's already an entry with the same {@code jobId} in the pool returns found entry
     * with {@code alreadyExisted} flag set to {@code true}, which means this job is already
     * being run by some other thread.
     *
     * @param jobId     job id to allocate {@code ContainerizedJob} object for
     * @param timeoutMs timeout for allocation
     * @return newly allocated job or job from the pool with the same {@code jobId}
     * @throws ContainerizedJobAllocationException when failed to allocate
     */
    ContainerizedJob tryAllocate(UUID jobId, long timeoutMs) throws ContainerizedJobAllocationException;

    /**
     * Removes {@code containerizedJob} from the pool, assuming it's been finished,
     * calling {@code reclaim()} on it, and freeing up space for one more job to allocate.
     * Doesn't do anything when {@code containerizedJob} not found.
     *
     * @param containerizedJob job to remove from the pool
     */
    void reclaim(ContainerizedJob containerizedJob);

    /**
     * Retrieves all allocated containerized jobs, that haven't been removed form the pool.
     *
     * @return all containerized jobs
     */
    List<ContainerizedJob> getContainerizedJobs();
}
