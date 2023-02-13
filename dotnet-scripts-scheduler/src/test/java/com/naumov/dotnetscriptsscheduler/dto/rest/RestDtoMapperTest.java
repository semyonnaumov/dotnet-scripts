package com.naumov.dotnetscriptsscheduler.dto.rest;

import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobCreateRequest;
import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayloadConfig;
import com.naumov.dotnetscriptsscheduler.dto.rest.rs.*;
import com.naumov.dotnetscriptsscheduler.model.*;
import com.naumov.dotnetscriptsscheduler.model.JobResult;
import com.naumov.dotnetscriptsscheduler.model.JobStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.naumov.dotnetscriptsscheduler.dto.rest.rs.JobStatus.FINISHED;
import static org.junit.jupiter.api.Assertions.*;

class RestDtoMapperTest {
    private final RestDtoMapper dtoMapper = new RestDtoMapper();

    @Test
    void testFromJobCreateRequestRegular() {
        JobRequestPayloadConfig jobRequestPayloadConfig = new JobRequestPayloadConfig();
        jobRequestPayloadConfig.setNugetConfigXml("<config/>");

        com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload jobRequestPayload =
                new com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload();
        jobRequestPayload.setScript("script");
        jobRequestPayload.setJobConfig(jobRequestPayloadConfig);
        jobRequestPayload.setAgentType("some-agent");

        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setRequestId("111");
        jobCreateRequest.setSenderId("222");
        jobCreateRequest.setPayload(jobRequestPayload);

        JobRequest jobRequest = dtoMapper.fromJobCreateRequest(jobCreateRequest);
        assertNotNull(jobRequest);
        assertNull(jobRequest.getId());
        assertEquals("111", jobRequest.getMessageId());
        assertEquals("222", jobRequest.getSenderId());

        com.naumov.dotnetscriptsscheduler.model.JobRequestPayload payload = jobRequest.getPayload();
        assertNotNull(payload);
        assertNull(payload.getId());
        assertEquals("script", payload.getScript());
        assertEquals("some-agent", payload.getAgentType());

        JobPayloadConfig jobPayloadConfig = payload.getJobPayloadConfig();
        assertNotNull(jobPayloadConfig);
        assertEquals("<config/>", jobPayloadConfig.getNugetConfigXml());
    }

    @Test
    void testFromJobCreateRequestNull() {
        JobRequest jobRequest = dtoMapper.fromJobCreateRequest(null);
        assertNull(jobRequest);
    }

    @Test
    void testFromJobCreateRequestIncomplete() {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setRequestId("111");
        jobCreateRequest.setSenderId("222");

        JobRequest jobRequest = dtoMapper.fromJobCreateRequest(jobCreateRequest);
        assertNotNull(jobRequest);
        assertNull(jobRequest.getId());
        assertEquals("111", jobRequest.getMessageId());
        assertEquals("222", jobRequest.getSenderId());
        assertNull(jobRequest.getPayload());
    }

    @Test
    void testToJobCreateResponseRegular() {
        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");
        Job job = Job.builder().id(uuid).build();

        JobCreateResponse jobCreateResponse = dtoMapper.toJobCreateResponse(job);
        assertNotNull(jobCreateResponse);
        assertEquals(uuid, jobCreateResponse.getJobId());
    }

    @Test
    void testToJobCreateResponseNull() {
        JobCreateResponse jobCreateResponse = dtoMapper.toJobCreateResponse(null);
        assertNull(jobCreateResponse);
    }

    @Test
    void testToJobGetResponseRegular() {
        JobPayloadConfig jobPayloadConfig = JobPayloadConfig.builder()
                .nugetConfigXml("<config/>")
                .build();

        JobRequestPayload jobRequestPayload = JobRequestPayload.builder()
                .id(12345L)
                .script("script")
                .jobPayloadConfig(jobPayloadConfig)
                .agentType("some-agent")
                .build();

        JobRequest jobRequest = JobRequest.builder()
                .id(54321L)
                .messageId("111")
                .senderId("222")
                .payload(jobRequestPayload)
                .build();

        JobResult jobResult = JobResult.builder()
                .id(11111L)
                .finishedWith(JobResult.JobCompletionStatus.SUCCEEDED)
                .stdout("stdout")
                .stderr("stderr")
                .build();

        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");
        Job job = Job.builder()
                .id(uuid)
                .request(jobRequest)
                .status(JobStatus.FINISHED)
                .result(jobResult)
                .build();

        JobGetResponse jobGetResponse = dtoMapper.toJobGetResponse(job);
        assertNotNull(jobGetResponse);
        assertEquals(uuid, jobGetResponse.getJobId());
        assertNotNull(jobGetResponse.getRequest());

        JobCreateRequest request = jobGetResponse.getRequest();
        assertEquals("111", request.getRequestId());
        assertEquals("222", request.getSenderId());
        assertNotNull(request.getPayload());

        com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload payload = request.getPayload();
        assertEquals("script", payload.getScript());
        assertNotNull(payload.getJobConfig());

        JobRequestPayloadConfig jobConfig = payload.getJobConfig();
        assertEquals("<config/>", jobConfig.getNugetConfigXml());

        assertEquals("some-agent", payload.getAgentType());

        assertEquals(FINISHED, jobGetResponse.getStatus());

        assertNotNull(jobGetResponse.getResult());
        com.naumov.dotnetscriptsscheduler.dto.rest.rs.JobResult result = jobGetResponse.getResult();
        assertEquals(JobCompletionStatus.SUCCEEDED, result.getFinishedWith());
        assertEquals("stdout", result.getStdout());
        assertEquals("stderr", result.getStderr());
    }

    @Test
    void testToJobGetResponseNull() {
        JobGetResponse jobGetResponse = dtoMapper.toJobGetResponse(null);
        assertNull(jobGetResponse);
    }

    @Test
    void testToJobGetRequestResponseRegular() {
        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");

        JobPayloadConfig jobPayloadConfig = JobPayloadConfig.builder()
                .nugetConfigXml("<config/>")
                .build();

        JobRequestPayload jobRequestPayload = JobRequestPayload.builder()
                .id(12345L)
                .script("script")
                .jobPayloadConfig(jobPayloadConfig)
                .agentType("some-agent")
                .build();

        JobRequest jobRequest = JobRequest.builder()
                .id(54321L)
                .messageId("111")
                .senderId("222")
                .payload(jobRequestPayload)
                .build();

        JobGetRequestResponse jobGetRequestResponse = dtoMapper.toJobGetRequestResponse(uuid, jobRequest);
        assertNotNull(jobGetRequestResponse);
        assertEquals(uuid, jobGetRequestResponse.getJobId());
        assertNotNull(jobGetRequestResponse.getRequest());

        JobCreateRequest request = jobGetRequestResponse.getRequest();
        assertEquals("111", request.getRequestId());
        assertEquals("222", request.getSenderId());
        assertNotNull(request.getPayload());

        com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobRequestPayload payload = request.getPayload();
        assertEquals("script", payload.getScript());
        assertNotNull(payload.getJobConfig());

        JobRequestPayloadConfig jobConfig = payload.getJobConfig();
        assertEquals("<config/>", jobConfig.getNugetConfigXml());

        assertEquals("some-agent", payload.getAgentType());
    }

    @Test
    void testToJobGetRequestResponseNull() {
        JobGetRequestResponse jobGetRequestResponse = dtoMapper.toJobGetRequestResponse(null, null);
        assertNull(jobGetRequestResponse);
    }

    @Test
    void testToJobGetStatusResponseRegular() {
        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");

        JobGetStatusResponse jobGetStatusResponse = dtoMapper.toJobGetStatusResponse(uuid, JobStatus.FINISHED);
        assertNotNull(jobGetStatusResponse);
        assertEquals(uuid, jobGetStatusResponse.getJobId());
        assertEquals(FINISHED, jobGetStatusResponse.getStatus());
    }

    @Test
    void testToJobGetStatusResponseNull() {
        JobGetStatusResponse jobGetStatusResponse = dtoMapper.toJobGetStatusResponse(null, null);
        assertNull(jobGetStatusResponse);
    }

    @Test
    void testToJobGetResultResponseRegular() {
        JobResult jobResult = JobResult.builder()
                .id(11111L)
                .finishedWith(JobResult.JobCompletionStatus.SUCCEEDED)
                .stdout("stdout")
                .stderr("stderr")
                .build();

        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");

        JobGetResultResponse jobGetResultResponse = dtoMapper.toJobGetResultResponse(uuid, jobResult);

        assertNotNull(jobGetResultResponse);
        assertEquals(uuid, jobGetResultResponse.getJobId());
        assertNotNull(jobGetResultResponse.getResult());
        com.naumov.dotnetscriptsscheduler.dto.rest.rs.JobResult result = jobGetResultResponse.getResult();
        assertEquals(JobCompletionStatus.SUCCEEDED, result.getFinishedWith());
        assertEquals("stdout", result.getStdout());
        assertEquals("stderr", result.getStderr());
    }

    @Test
    void testToJobGetResultResponseNull() {
        JobGetResultResponse jobGetResultResponse = dtoMapper.toJobGetResultResponse(null, null);
        assertNull(jobGetResultResponse);
    }
}