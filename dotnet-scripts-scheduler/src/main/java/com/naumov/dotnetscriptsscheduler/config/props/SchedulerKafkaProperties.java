package com.naumov.dotnetscriptsscheduler.config.props;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("scheduler.kafka")
@Component
public class SchedulerKafkaProperties {
    @NotBlank
    private String brokerUrl;
    @NotNull
    @Min(1)
    private Integer reconnectBackoffMs;
    @NotNull
    @Min(1)
    private Integer reconnectBackoffMaxMs;
    @NotBlank
    private String jobsTopicPrefix;
    @NotBlank
    private String runningTopicName;
    @NotBlank
    private String finishedTopicName;
    @NotBlank
    private String producerAcks;
    @NotBlank
    private String consumerGroup;
}