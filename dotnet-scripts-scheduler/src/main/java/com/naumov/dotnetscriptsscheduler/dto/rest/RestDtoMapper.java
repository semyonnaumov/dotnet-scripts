package com.naumov.dotnetscriptsscheduler.dto.rest;

import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobCreateRequest;
import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayloadConfig;
import com.naumov.dotnetscriptsscheduler.dto.rest.rs.*;
import com.naumov.dotnetscriptsscheduler.model.Job;
import com.naumov.dotnetscriptsscheduler.model.JobPayloadConfig;
import com.naumov.dotnetscriptsscheduler.model.JobRequest;
import com.naumov.dotnetscriptsscheduler.model.JobRequestPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestDtoMapper {

    // -------------------------------------------- "From" mappings ------------------------------------------------- //
    public JobRequest fromJobCreateRequest(JobCreateRequest jobCreateRequest) {
        if (jobCreateRequest == null) return null;

        return JobRequest.builder()
                .messageId(jobCreateRequest.getRequestId())
                .senderId(jobCreateRequest.getSenderId())
                .payload(fromJobRequestPayload(jobCreateRequest.getPayload()))
                .build();
    }

    private JobRequestPayload fromJobRequestPayload(com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload payload) {
        if (payload == null) return null;

        return JobRequestPayload.builder()
                .script(payload.getScript())
                .jobPayloadConfig(fromJobRequestPayloadConfig(payload.getJobConfig()))
                .agentType(payload.getAgentType())
                .build();
    }

    private JobPayloadConfig fromJobRequestPayloadConfig(JobRequestPayloadConfig jobConfig) {
        if (jobConfig == null) return null;

        return JobPayloadConfig.builder()
                .nugetConfigXml(jobConfig.getNugetConfigXml())
                .build();
    }

    // -------------------------------------------- "To" mappings --------------------------------------------------- //
    public JobCreateResponse toJobCreateResponse(Job job) {
        if (job == null) return null;

        return JobCreateResponse.builder()
                .jobId(job.getId())
                .build();
    }

    public JobGetResponse toJobGetResponse(Job job) {
        if (job == null) return null;

        return JobGetResponse.builder()
                .jobId(job.getId())
                .request(toJobRequest(job.getRequest()))
                .status(toJobStatus(job.getStatus()))
                .result(toJobResult(job.getResult()))
                .build();
    }

    public JobGetRequestResponse toJobGetRequestResponse(UUID jobId, JobRequest jobRequest) {
        if (jobId == null && jobRequest == null) return null;

        return JobGetRequestResponse.builder()
                .jobId(jobId)
                .request(toJobRequest(jobRequest))
                .build();
    }

    public JobGetStatusResponse toJobGetStatusResponse(UUID jobId, com.naumov.dotnetscriptsscheduler.model.JobStatus jobStatus) {
        if (jobId == null && jobStatus == null) return null;

        return JobGetStatusResponse.builder()
                .jobId(jobId)
                .status(toJobStatus(jobStatus))
                .build();
    }

    public JobGetResultResponse toJobGetResultResponse(UUID jobId, com.naumov.dotnetscriptsscheduler.model.JobResult jobResult) {
        if (jobId == null && jobResult == null) return null;

        return JobGetResultResponse.builder()
                .jobId(jobId)
                .result(toJobResult(jobResult))
                .build();
    }

    private JobCreateRequest toJobRequest(JobRequest request) {
        if (request == null) return null;

        return JobCreateRequest.builder()
                .requestId(request.getMessageId())
                .senderId(request.getSenderId())
                .payload(toJobRequestPayload(request.getPayload()))
                .build();
    }

    private com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload toJobRequestPayload(JobRequestPayload payload) {
        if (payload == null) return null;

        return com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload.builder()
                .script(payload.getScript())
                .jobConfig(toJobConfig(payload.getJobPayloadConfig()))
                .agentType(payload.getAgentType())
                .build();
    }

    private JobRequestPayloadConfig toJobConfig(JobPayloadConfig jobPayloadConfig) {
        if (jobPayloadConfig == null) return null;

        return JobRequestPayloadConfig.builder()
                .nugetConfigXml(jobPayloadConfig.getNugetConfigXml())
                .build();
    }

    private JobStatus toJobStatus(com.naumov.dotnetscriptsscheduler.model.JobStatus status) {
        if (status == null) return null;

        try {
            return JobStatus.valueOf(status.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unable to map " + com.naumov.dotnetscriptsscheduler.model.JobStatus.class.getName() + " to " +
                    JobStatus.class.getName() + " from value " + status);
        }
    }

    private JobResult toJobResult(com.naumov.dotnetscriptsscheduler.model.JobResult result) {
        if (result == null) return null;

        return JobResult.builder()
                .finishedWith(toJobCompletionStatus(result.getFinishedWith()))
                .stdout(result.getStdout())
                .stderr(result.getStderr())
                .build();
    }

    private JobCompletionStatus toJobCompletionStatus(com.naumov.dotnetscriptsscheduler.model.JobResult.JobCompletionStatus finishedWith) {
        if (finishedWith == null) return null;

        try {
            return JobCompletionStatus.valueOf(finishedWith.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unable to map " +
                    com.naumov.dotnetscriptsscheduler.model.JobResult.JobCompletionStatus.class.getName() + " to " +
                    JobCompletionStatus.class.getName() + " from value " + finishedWith);
        }
    }
}