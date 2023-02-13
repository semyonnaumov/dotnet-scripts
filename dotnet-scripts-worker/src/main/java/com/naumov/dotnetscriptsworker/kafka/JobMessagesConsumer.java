package com.naumov.dotnetscriptsworker.kafka;

import com.naumov.dotnetscriptsworker.dto.KafkaDtoMapper;
import com.naumov.dotnetscriptsworker.dto.cons.JobTaskMessage;
import com.naumov.dotnetscriptsworker.service.JobService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JobMessagesConsumer {
    private static final Logger LOGGER = LogManager.getLogger(JobMessagesConsumer.class);
    private final JobService jobService;
    private final KafkaDtoMapper kafkaDtoMapper;

    @Autowired
    public JobMessagesConsumer(JobService jobService,
                               KafkaDtoMapper kafkaDtoMapper) {
        this.jobService = jobService;
        this.kafkaDtoMapper = kafkaDtoMapper;
    }

    @KafkaListener(
            topics = "${worker.kafka.jobs-topic-name}",
            containerFactory = "kafkaListenerContainerFactory",
            errorHandler = "kafkaListenerPayloadValidationErrorHandler"
    )
    public void onJobTaskMessage(@Payload @Valid JobTaskMessage jobTaskMessage, Acknowledgment ack) {
        ack.acknowledge();
        UUID messageId = UUID.randomUUID();
        LOGGER.info("Received job {} task message, assigned message id {}", jobTaskMessage.getJobId(), messageId);
        try {
            jobService.runJob(kafkaDtoMapper.fromJobTaskMessage(jobTaskMessage, messageId));
        } catch (RuntimeException e) {
            LOGGER.error("Failed to run job for job task {} with message id {}", jobTaskMessage.getJobId(), messageId);
            throw e;
        }
    }
}
