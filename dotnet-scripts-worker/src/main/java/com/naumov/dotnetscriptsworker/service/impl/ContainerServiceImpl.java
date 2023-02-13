package com.naumov.dotnetscriptsworker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import com.naumov.dotnetscriptsworker.config.props.SandboxContainerProperties;
import com.naumov.dotnetscriptsworker.service.ContainerService;
import com.naumov.dotnetscriptsworker.service.exception.ContainerServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ContainerServiceImpl implements ContainerService {
    private static final Logger LOGGER = LogManager.getLogger(ContainerServiceImpl.class);
    private static final int IMAGE_PULL_TIMEOUT_SEC = 120;
    private static final long MEGABYTE_MULTIPLIER = 1024 * 1024;
    private static final String NO_NEW_PRIVILEGES_SECURITY_OPT = "no-new-privileges";
    private static final String STORAGE_OPT_SIZE = "size";
    private final DockerClient dockerClient;
    private final SandboxContainerProperties sandboxContainerProperties;

    @Autowired
    public ContainerServiceImpl(DockerClient dockerClient,
                                SandboxContainerProperties sandboxContainerProperties) {
        this.dockerClient = dockerClient;
        this.sandboxContainerProperties = sandboxContainerProperties;
    }

    @PostConstruct
    public void init() {
        try {
            LOGGER.info("{} initialization: pulling sandbox image", ContainerServiceImpl.class.getSimpleName());
            pullSandboxImage();
            LOGGER.info("{} initialization: finished pulling sandbox image", ContainerServiceImpl.class.getSimpleName());
        } catch (Exception e) {
            LOGGER.error("{} initialization: failed pulling sandbox image", ContainerServiceImpl.class.getSimpleName(), e);
            throw new ContainerServiceException("Failed to pull", e);
        }
    }

    private void pullSandboxImage() throws InterruptedException {
        LOGGER.info("Pulling image {}", sandboxContainerProperties.getImage());
        boolean successfullyPulled = dockerClient.pullImageCmd(sandboxContainerProperties.getImageName())
                .withTag(sandboxContainerProperties.getImageTag())
                .exec(new ResultCallback.Adapter<PullResponseItem>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        if (item.isPullSuccessIndicated()) {
                            LOGGER.info("Successfully pulled {}", item.getId());
                        } else if (item.isErrorIndicated()) {
                            LOGGER.error("Failed to pull {}: {}", item.getId(), item.getErrorDetail());
                        }
                    }
                })
                .awaitCompletion(IMAGE_PULL_TIMEOUT_SEC, TimeUnit.SECONDS);
        if (!successfullyPulled) {
            throw new ContainerServiceException("Failed to pull image " + sandboxContainerProperties.getImage());
        }
    }

    @Override
    public List<String> getAllContainersIds() {
        return getAllContainers().stream()
                .map(Container::getId)
                .toList();
    }

    @Override
    public List<String> getAllContainersIdsWithNamePrefix(String namePrefix) {
        Objects.requireNonNull(namePrefix, "Parameter namePrefix must not nbe null");
        return getAllContainers().stream()
                .filter(c -> {
                    if (c.getNames() == null) return false;
                    return Arrays.stream(c.getNames())
                            .filter(Objects::nonNull)
                            .anyMatch(n -> n.startsWith(namePrefix));
                })
                .map(Container::getId)
                .toList();
    }

    private List<Container> getAllContainers() {
        try {
            return dockerClient.listContainersCmd().exec();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to get all containers", e);
            throw new ContainerServiceException("Failed to get all containers", e);
        }
    }

    @Override
    public String createContainer(String containerName,
                                  String sandboxImageName,
                                  String volumeSrcPath,
                                  String volumeDestPath,
                                  String scriptFileName) {
        try {
            String volumeBindDescriptor = getVolumeBindDescriptor(volumeSrcPath, volumeDestPath);
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withSecurityOpts(List.of(NO_NEW_PRIVILEGES_SECURITY_OPT))
                    .withBinds(Bind.parse(volumeBindDescriptor));

            if (sandboxContainerProperties.getEnableResourceLimits()) {
                hostConfig.withMemory(sandboxContainerProperties.getMemoryMb() * MEGABYTE_MULTIPLIER)
                        .withMemoryReservation(sandboxContainerProperties.getMemoryReservationMb() * MEGABYTE_MULTIPLIER)
                        .withCpuPeriod(sandboxContainerProperties.getCpuPeriodMicros())
                        .withCpuQuota(sandboxContainerProperties.getCpuQuotaMicros())
                        .withCpuShares(sandboxContainerProperties.getCpuShares())
                        .withPidsLimit(sandboxContainerProperties.getPidsLimit())
                        .withBlkioWeight(sandboxContainerProperties.getBlkioWeight());
                        // TODO not working: com.github.dockerjava.api.exception.InternalServerErrorException: Status 500: {"message":"--storage-opt is supported only for overlay over xfs with 'pquota' mount option"}
//                        .withStorageOpt(Collections.singletonMap(STORAGE_OPT_SIZE, sandboxContainerProperties.getStorageSize()))
            }

            CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(sandboxImageName)
                    .withEntrypoint("dotnet-script", volumeDestPath + "/" + scriptFileName, "-c", "release")
                    .withName(containerName)
                    .withHostConfig(hostConfig)
                    .exec();

            String containerId = createContainerResponse.getId();
            LOGGER.info("Created container {}, (name={}, image={}, volume={})",
                    containerId, containerName, sandboxImageName, volumeBindDescriptor);

            return containerId;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to create container with name {}", containerName, e);
            throw new ContainerServiceException("Failed to create container with name " + containerName, e);
        }
    }

    private String getVolumeBindDescriptor(String volumeSrcPath, String volumeDestPath) {
        return volumeSrcPath + ":" + volumeDestPath + ":ro";
    }

    @Override
    public void startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            LOGGER.info("Started container {}", containerId);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start container {}", containerId, e);
            throw new ContainerServiceException("Failed to start container " + containerId, e);
        }
    }

    @Override
    public boolean isRunning(String containerId) {
        try {
            InspectContainerResponse inspectContainerResponse = dockerClient.inspectContainerCmd(containerId)
                    .exec();
            return Boolean.TRUE.equals(inspectContainerResponse.getState().getRunning());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to check whether container {} is running", containerId, e);
            throw new ContainerServiceException("Failed to check whether container " + containerId + " is running", e);
        }
    }

    @Override
    public void stopContainer(String containerId, boolean mustExist) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            LOGGER.info("Stopped container {}", containerId);
        } catch (NotModifiedException ignored) {
            // already stopped
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException && !mustExist) return;

            LOGGER.error("Failed to stop container {}", containerId, e);
            throw new ContainerServiceException("Failed to stop container " + containerId, e);
        }
    }

    @Override
    public String getStdout(String containerId, long timeoutMs) {
        return getLogs(containerId, true, timeoutMs);
    }

    @Override
    public String getStderr(String containerId, long timeoutMs) {
        return getLogs(containerId, false, timeoutMs);
    }

    private String getLogs(String containerId, boolean isStdout, long timeoutMs) {
        try {
            List<String> logLines = new ArrayList<>();
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(isStdout)
                    .withStdErr(!isStdout)
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame item) {
                            logLines.add(item.toString());
                        }
                    })
                    .awaitCompletion(timeoutMs, TimeUnit.MILLISECONDS);

            LOGGER.info("Received logs for container {}", containerId);
            return String.join(System.lineSeparator(), logLines);

        } catch (RuntimeException | InterruptedException e) {
            String logsType = isStdout ? "STDOUT" : "STDERR";
            LOGGER.error("Failed to receive logs ({}) for container {}", logsType, containerId, e);
            throw new ContainerServiceException("Failed to receive logs ({" + logsType + "}) for container " + containerId, e);
        }
    }

    @Override
    public Long getExitCode(String containerId) {
        try {
            InspectContainerResponse rs = dockerClient.inspectContainerCmd(containerId).exec();
            LOGGER.info("Received exit code for container {}", containerId);
            return rs.getState().getExitCodeLong();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to receive exit code for container {}", containerId, e);
            throw new ContainerServiceException("Failed to receive exit code for container {}" + containerId, e);
        }
    }

    @Override
    public void removeContainer(String containerId, boolean mustExist) {
        try {
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .exec();

            LOGGER.info("Removed container {}", containerId);
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException && !mustExist) return;

            LOGGER.error("Failed to remove container {}", containerId, e);
            throw new ContainerServiceException("Failed to remove container " + containerId, e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            dockerClient.close();
        } catch (IOException e) {
            throw new ContainerServiceException("Failed to close " + dockerClient.getClass().getSimpleName(), e);
        }
    }
}
