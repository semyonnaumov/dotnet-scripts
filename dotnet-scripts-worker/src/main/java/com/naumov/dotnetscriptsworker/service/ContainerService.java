package com.naumov.dotnetscriptsworker.service;

import java.util.List;

public interface ContainerService {

    List<String> getAllContainersIds();

    List<String> getAllContainersIdsWithNamePrefix(String prefix);

    String createContainer(String containerName,
                           String sandboxImageName,
                           String volumeSrcPath,
                           String volumeDestPath,
                           String scriptFileName);

    void startContainer(String containerId);

    boolean isRunning(String containerId);

    void stopContainer(String containerId, boolean mustExist);

    String getStdout(String containerId, long timeoutMs);

    String getStderr(String containerId, long timeoutMs);

    Long getExitCode(String containerId);

    void removeContainer(String containerId, boolean mustExist);
}
