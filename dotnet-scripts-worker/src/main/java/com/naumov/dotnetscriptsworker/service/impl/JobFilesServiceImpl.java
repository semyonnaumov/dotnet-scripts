package com.naumov.dotnetscriptsworker.service.impl;

import com.naumov.dotnetscriptsworker.config.props.SandboxProperties;
import com.naumov.dotnetscriptsworker.model.JobTask;
import com.naumov.dotnetscriptsworker.service.JobFilesService;
import com.naumov.dotnetscriptsworker.service.exception.ScriptFilesServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class JobFilesServiceImpl implements JobFilesService {
    private static final Logger LOGGER = LogManager.getLogger(JobFilesServiceImpl.class);
    private final SandboxProperties sandboxProperties;

    @Autowired
    public JobFilesServiceImpl(SandboxProperties sandboxProperties) {
        this.sandboxProperties = sandboxProperties;
    }

    @Override
    public String prepareJobFiles(JobTask jobTask) {
        UUID jobId = jobTask.getJobId();
        try {
            Path tempDirPath = getJobTempDirectoryPath(jobId);
            Path scriptFilePath = getJobTempDirectoryFilePath(jobId, sandboxProperties.getJobScriptFileName());
            Path nugetConfigFilePath = getJobTempDirectoryFilePath(jobId, "NuGet.Config");

            Files.createDirectories(tempDirPath);
            createAndWriteFile(scriptFilePath, jobTask.getJobScript());
            if (jobTask.getJobConfig() != null && jobTask.getJobConfig().getNugetConfigXml() != null) {
                createAndWriteFile(nugetConfigFilePath, jobTask.getJobConfig().getNugetConfigXml());
                LOGGER.debug("Created Nuget.Config file for job {} at {}", jobId, nugetConfigFilePath);
            }

            LOGGER.info("Prepared files for job {} at {}", jobId, tempDirPath);
            return tempDirPath.toString();
        } catch (IOException e) {
            LOGGER.error("Failed to prepare files for job {}", jobId, e);
            throw new ScriptFilesServiceException("Failed to prepare files for job " + jobId, e);
        }
    }

    @Override
    public void cleanupJobFiles(UUID jobId) {
        Path tempDirPath = getJobTempDirectoryPath(jobId);
        try (Stream<Path> pathStream = Files.walk(tempDirPath)) {
            List<Path> pathsToDelete = pathStream.sorted(Comparator.reverseOrder()).toList();
            for (Path pathToDelete : pathsToDelete) {
                Files.deleteIfExists(pathToDelete);
            }
            LOGGER.info("Deleted temp files directory {} for job {}", tempDirPath, jobId);
        } catch (IOException e) {
            LOGGER.error("Failed to delete temp files directory {} for job {}", tempDirPath, jobId, e);
            throw new ScriptFilesServiceException("Failed to cleanup script files for job " + jobId, e);
        }
    }

    private static void createAndWriteFile(Path filePath, String contents) throws IOException {
        Files.createFile(filePath);
        Files.writeString(filePath, contents);
    }

    private Path getJobTempDirectoryPath(UUID jobId) {
        return Paths.get(sandboxProperties.getJobFilesHostDir(), jobId.toString())
                .toAbsolutePath();
    }

    private Path getJobTempDirectoryFilePath(UUID jobId, String fileName) {
        return Paths.get(sandboxProperties.getJobFilesHostDir(), jobId.toString(), fileName)
                .toAbsolutePath();
    }
}
