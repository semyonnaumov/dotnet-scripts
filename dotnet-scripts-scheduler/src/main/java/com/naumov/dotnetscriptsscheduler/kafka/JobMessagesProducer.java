package com.naumov.dotnetscriptsscheduler.kafka;

import com.naumov.dotnetscriptsscheduler.config.props.SchedulerKafkaProperties;
import com.naumov.dotnetscriptsscheduler.dto.kafka.KafkaDtoMapper;
import com.naumov.dotnetscriptsscheduler.dto.kafka.prod.JobTaskMessage;
import com.naumov.dotnetscriptsscheduler.exception.BadInputException;
import com.naumov.dotnetscriptsscheduler.exception.JobMessagesProducerException;
import com.naumov.dotnetscriptsscheduler.model.Job;
import com.naumov.dotnetscriptsscheduler.service.WorkerTypesService;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class JobMessagesProducer {
    private static final Logger LOGGER = LogManager.getLogger(JobMessagesProducer.class);
    private final SchedulerKafkaProperties kafkaProperties;
    private final WorkerTypesService workerTypesService;
    private final KafkaTemplate<String, JobTaskMessage> jobTaskKafkaTemplate;
    private final KafkaDtoMapper kafkaDtoMapper;

    @Autowired
    public JobMessagesProducer(SchedulerKafkaProperties kafkaProperties,
                               WorkerTypesService workerTypesService,
                               KafkaTemplate<String, JobTaskMessage> jobTaskKafkaTemplate,
                               KafkaDtoMapper kafkaDtoMapper) {
        this.kafkaProperties = kafkaProperties;
        this.workerTypesService = workerTypesService;
        this.jobTaskKafkaTemplate = jobTaskKafkaTemplate;
        this.kafkaDtoMapper = kafkaDtoMapper;
    }

    public void sendJobTaskMessageAsync(Job job) {
        String topicName = getTopicName(job);
        JobTaskMessage jobTaskMessage = kafkaDtoMapper.toJobTaskMessage(job);

        LOGGER.debug("Sending job task message {} for job {}", jobTaskMessage, job.getId());
        jobTaskKafkaTemplate.send(topicName, jobTaskMessage)
                .thenAccept(res -> {
                    LOGGER.info("Sent task for job {} to topic {}", jobTaskMessage.getJobId(), topicName);
                }).exceptionally(e -> {
                    LOGGER.error("Failed to send task for job {} to topic {}", jobTaskMessage.getJobId(), topicName, e);
                    return null;
                });
    }

    private String getTopicName(Job job) {
        Objects.requireNonNull(job, "Parameter job must not be null");
        UUID jobId = job.getId();
        if (job.getRequest() == null || job.getRequest().getPayload() == null ||
                job.getRequest().getPayload().getAgentType() == null) {
            LOGGER.error("Failed to get topic name for job {}: agentType is null", jobId);
            throw new JobMessagesProducerException("Failed to get topic name for job " + jobId + ": agentType is null");
        }

        String workerType = job.getRequest().getPayload().getAgentType();
        if (!workerTypesService.workerExists(workerType)) {
            LOGGER.error("Wrong worker type {}: available types are {}", workerType,
                    workerTypesService.getAllWorkerTypes());
            throw new BadInputException("Wrong worker type " + workerType + ": available types are " +
                    workerTypesService.getAllWorkerTypes());
        }

        return kafkaProperties.getJobsTopicPrefix() + workerType;
    }

    @PreDestroy
    void shutdown() {
        // only needed if producer per thread enabled
        jobTaskKafkaTemplate.getProducerFactory().closeThreadBoundProducer();
    }
}