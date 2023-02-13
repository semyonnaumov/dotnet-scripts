package com.naumov.dotnetscriptsscheduler.dto.kafka;

import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobCompletionStatus;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobFinishedMessage;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobStatus;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.ScriptResults;
import com.naumov.dotnetscriptsscheduler.dto.kafka.prod.JobTaskMessage;
import com.naumov.dotnetscriptsscheduler.model.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KafkaDtoMapperTest {
    private final KafkaDtoMapper kafkaDtoMapper = new KafkaDtoMapper();

    @Test
    public void testFromJobFinishedMessageRegular() {
        ScriptResults scriptResults = new ScriptResults();
        scriptResults.setFinishedWith(JobCompletionStatus.SUCCEEDED);
        scriptResults.setStdout("stdout");
        scriptResults.setStderr("stderr");

        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");
        JobFinishedMessage jobFinishedMessage = new JobFinishedMessage();
        jobFinishedMessage.setJobId(uuid);
        jobFinishedMessage.setStatus(JobStatus.ACCEPTED);
        jobFinishedMessage.setScriptResults(scriptResults);

        Job job = kafkaDtoMapper.fromJobFinishedMessage(jobFinishedMessage);
        assertNotNull(job);
        assertEquals(uuid, job.getId());
        assertEquals(com.naumov.dotnetscriptsscheduler.model.JobStatus.FINISHED, job.getStatus());
        assertNotNull(job.getResult());

        JobResult result = job.getResult();
        assertNull(result.getId());
        assertEquals(JobResult.JobCompletionStatus.SUCCEEDED, result.getFinishedWith());
        assertEquals("stdout", result.getStdout());
        assertEquals("stderr", result.getStderr());
    }

    @Test
    public void testFromJobFinishedMessageNull() {
        assertNull(kafkaDtoMapper.fromJobFinishedMessage(null));
    }

    @Test
    public void testToJobTaskMessageRegular() {
        JobPayloadConfig jobPayloadConfig = new JobPayloadConfig();
        jobPayloadConfig.setNugetConfigXml("<config/>");

        JobRequestPayload jobRequestPayload = new JobRequestPayload();
        jobRequestPayload.setScript("script");
        jobRequestPayload.setJobPayloadConfig(jobPayloadConfig);
        jobRequestPayload.setAgentType("some-agent");

        JobRequest jobRequest = new JobRequest();
        jobRequest.setMessageId("111");
        jobRequest.setSenderId("222");
        jobRequest.setPayload(jobRequestPayload);

        UUID uuid = UUID.fromString("7f000001-8637-1fc4-8186-374017c10000");
        Job job = new Job();
        job.setId(uuid);
        job.setRequest(jobRequest);

        JobTaskMessage jobTaskMessage = kafkaDtoMapper.toJobTaskMessage(job);
        assertNotNull(jobTaskMessage);
        assertEquals(uuid, jobTaskMessage.getJobId());
        assertEquals("script", jobTaskMessage.getScript());
        assertNotNull(jobTaskMessage.getJobConfig());
        assertEquals("<config/>", jobTaskMessage.getJobConfig().getNugetConfigXml());
    }

    @Test
    public void testToJobTaskMessageNull() {
        assertNull(kafkaDtoMapper.toJobTaskMessage(null));
    }
}