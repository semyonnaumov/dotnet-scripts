package com.naumov.dotnetscriptsworker.kafka;

import com.naumov.dotnetscriptsworker.config.props.WorkerKafkaProperties;
import com.naumov.dotnetscriptsworker.dto.KafkaDtoMapper;
import com.naumov.dotnetscriptsworker.dto.prod.JobFinishedMessage;
import com.naumov.dotnetscriptsworker.dto.prod.JobStartedMessage;
import com.naumov.dotnetscriptsworker.model.JobResults;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JobMessagesProducer {
    private static final Logger LOGGER = LogManager.getLogger(JobMessagesProducer.class);
    private final WorkerKafkaProperties kafkaProperties;
    private final KafkaTemplate<String, JobStartedMessage> jobStartedKafkaTemplate;
    private final KafkaTemplate<String, JobFinishedMessage> jobFinishedKafkaTemplate;
    private final KafkaDtoMapper dtoMapper;

    @Autowired
    public JobMessagesProducer(WorkerKafkaProperties kafkaProperties,
                               KafkaTemplate<String, JobStartedMessage> jobStartedKafkaTemplate,
                               KafkaTemplate<String, JobFinishedMessage> jobFinishedKafkaTemplate,
                               KafkaDtoMapper dtoMapper) {
        this.kafkaProperties = kafkaProperties;
        this.jobStartedKafkaTemplate = jobStartedKafkaTemplate;
        this.jobFinishedKafkaTemplate = jobFinishedKafkaTemplate;
        this.dtoMapper = dtoMapper;
    }

    public void sendJobStartedMessageAsync(UUID jobId) {
        String runningTopic = kafkaProperties.getRunningTopicName();
        JobStartedMessage jobStartedMessage = dtoMapper.toJobStartedMessage(jobId);

        LOGGER.debug("Sending job started message {} for job {}", jobStartedMessage, jobId);
        jobStartedKafkaTemplate.send(runningTopic, jobStartedMessage)
                .thenAccept(res -> {
                    LOGGER.info("Sent job {} started message to topic {}", jobId, runningTopic);
                }).exceptionally(e -> {
                    LOGGER.error("Failed to send job {} started message to topic {}", jobId, runningTopic, e);
                    return null;
                });
    }

    public void sentJobFinishedMessageAsync(JobResults jobResults) {
        JobFinishedMessage jobFinishedMessage = dtoMapper.toJobFinishedMessage(jobResults);
        String finishedTopic = kafkaProperties.getFinishedTopicName();

        LOGGER.debug("Sending job finished message {} for job {}",
                jobFinishedMessage, jobFinishedMessage.getJobId());

        jobFinishedKafkaTemplate.send(finishedTopic, jobFinishedMessage)
                .thenAccept(res -> {
                    LOGGER.info("Sent job {} finished message to topic {}",
                            jobFinishedMessage.getJobId(), finishedTopic);
                }).exceptionally(e -> {
                    LOGGER.error("Failed to send job {} finished message to topic {}",
                            jobFinishedMessage.getJobId(), finishedTopic, e);
                    return null;
                });
    }

    @PreDestroy
    void shutdown() {
        // only needed if producer per thread enabled
        jobStartedKafkaTemplate.getProducerFactory().closeThreadBoundProducer();
        jobFinishedKafkaTemplate.getProducerFactory().closeThreadBoundProducer();
    }
}
