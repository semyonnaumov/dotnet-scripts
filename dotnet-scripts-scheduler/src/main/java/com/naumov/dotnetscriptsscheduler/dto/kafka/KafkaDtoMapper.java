package com.naumov.dotnetscriptsscheduler.dto.kafka;

import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobCompletionStatus;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobFinishedMessage;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobStatus;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.ScriptResults;
import com.naumov.dotnetscriptsscheduler.dto.kafka.prod.JobConfig;
import com.naumov.dotnetscriptsscheduler.dto.kafka.prod.JobTaskMessage;
import com.naumov.dotnetscriptsscheduler.model.Job;
import com.naumov.dotnetscriptsscheduler.model.JobPayloadConfig;
import com.naumov.dotnetscriptsscheduler.model.JobRequestPayload;
import com.naumov.dotnetscriptsscheduler.model.JobResult;
import org.springframework.stereotype.Component;

@Component
public class KafkaDtoMapper {

    // -------------------------------------------- "From" mappings ------------------------------------------------- //
    public Job fromJobFinishedMessage(JobFinishedMessage jobFinishedMessage) {
        if (jobFinishedMessage == null) return null;

        Job.JobBuilder jobBuilder = Job.builder()
                .id(jobFinishedMessage.getJobId());

        JobStatus jobMessageStatus = jobFinishedMessage.getStatus();
        if (jobMessageStatus == JobStatus.ACCEPTED) {
            jobBuilder.status(com.naumov.dotnetscriptsscheduler.model.JobStatus.FINISHED)
                    .result(fromJobFinishedMessageScriptResults(jobFinishedMessage.getScriptResults()));
        } else if (jobMessageStatus == JobStatus.REJECTED) {
            jobBuilder.status(com.naumov.dotnetscriptsscheduler.model.JobStatus.REJECTED);
        } else {
            throw new IllegalStateException("Unable to map " + JobFinishedMessage.class.getName() + " to " +
                    Job.class.getName() + " with status " + jobMessageStatus);
        }

        return jobBuilder.build();
    }

    private JobResult fromJobFinishedMessageScriptResults(ScriptResults scriptResults) {
        if (scriptResults == null) return null;

        return JobResult.builder()
                .finishedWith(fromJobFinishedMessageScriptResultsFinishedWith(scriptResults.getFinishedWith()))
                .stdout(scriptResults.getStdout())
                .stderr(scriptResults.getStderr())
                .build();
    }

    private JobResult.JobCompletionStatus fromJobFinishedMessageScriptResultsFinishedWith(JobCompletionStatus finishedWith) {
        if (finishedWith == null) return null;

        try {
            return JobResult.JobCompletionStatus.valueOf(finishedWith.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unable to map " + JobCompletionStatus.class.getName() + " to " +
                    JobResult.JobCompletionStatus.class.getName() + " from value " + finishedWith);
        }
    }

    // -------------------------------------------- "To" mappings --------------------------------------------------- //
    public JobTaskMessage toJobTaskMessage(Job job) {
        if (job == null) return null;

        JobTaskMessage.JobTaskMessageBuilder builder = JobTaskMessage.builder().jobId(job.getId());
        if (job.getRequest() != null && job.getRequest().getPayload() != null) {
            JobRequestPayload payload = job.getRequest().getPayload();
            builder.script(payload.getScript())
                    .jobConfig(toJobConfig(payload.getJobPayloadConfig()));
        }

        return builder.build();
    }

    private JobConfig toJobConfig(JobPayloadConfig jobPayloadConfigJson) {
        if (jobPayloadConfigJson == null) return null;

        return JobConfig.builder()
                .nugetConfigXml(jobPayloadConfigJson.getNugetConfigXml())
                .build();
    }
}
