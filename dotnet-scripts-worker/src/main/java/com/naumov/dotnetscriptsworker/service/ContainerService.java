package com.naumov.dotnetscriptsworker.service;

import java.util.List;

/**
 * Service used to manipulate containers with job tasks.
 */
public interface ContainerService {

    /**
     * Lists all existing container ids.
     *
     * @return list of container ids
     */
    List<String> getAllContainersIds();

    /**
     * Lists all existing container ids with the specified name prefix.
     *
     * @param prefix container name prefix
     * @return list of container ids
     */
    List<String> getAllContainersIdsWithNamePrefix(String prefix);

    /**
     * Creates a job task container.
     *
     * @param containerName    name of job container
     * @param sandboxImageName image to create the container from
     * @param volumeSrcPath    host directory to mount (read only) on the container
     * @param volumeDestPath   destination of the mounted host directory in the container
     * @param scriptFileName   file with the script to run name
     * @return created container id
     */
    String createContainer(String containerName,
                           String sandboxImageName,
                           String volumeSrcPath,
                           String volumeDestPath,
                           String scriptFileName);


    /**
     * Starts the given container.
     *
     * @param containerId container to start
     */
    void startContainer(String containerId);

    /**
     * Checks whether the given container is running.
     *
     * @param containerId container to check
     */
    boolean isRunning(String containerId);

    /**
     * Stops the container. When {@code mustExist} is set to true and the container doesn't
     * exist throws an error, returns successfully otherwise.
     *
     * @param containerId container to stop
     * @param mustExist   fail when not exists flag
     */
    void stopContainer(String containerId, boolean mustExist);

    /**
     * Retrieves the STDOUT of the container in the blocking manner.
     *
     * @param containerId container to retrieve STDOUT from
     * @param timeoutMs   timeout to wait for while trying
     * @return STDOUT string
     */
    String getStdout(String containerId, long timeoutMs);

    /**
     * Retrieves the STDERR of the container in the blocking manner.
     *
     * @param containerId container to retrieve STDERR from
     * @param timeoutMs   timeout to wait for while trying
     * @return STDERR string
     */
    String getStderr(String containerId, long timeoutMs);

    /**
     * Retrieves the exit code of the container.
     *
     * @param containerId container to retrieve the exit code from
     * @return exit code
     */
    Long getExitCode(String containerId);

    /**
     * Removes the container. When {@code mustExist} is set to true and the container doesn't
     * exist throws an error, returns successfully otherwise.
     *
     * @param containerId container to remove
     * @param mustExist   fail when not exists flag
     */
    void removeContainer(String containerId, boolean mustExist);
}
