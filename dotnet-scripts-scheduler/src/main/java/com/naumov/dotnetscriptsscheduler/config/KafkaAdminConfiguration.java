package com.naumov.dotnetscriptsscheduler.config;

import com.naumov.dotnetscriptsscheduler.config.props.SchedulerKafkaAdminProperties;
import com.naumov.dotnetscriptsscheduler.service.WorkerTypesService;
import com.naumov.dotnetscriptsscheduler.service.impl.WorkerTypesServiceImpl;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.*;

@Configuration
public class KafkaAdminConfiguration {
    private static final Set<String> WORKER_TYPES = Set.of("linux-amd64-dotnet-7");
    private final SchedulerKafkaAdminProperties kafkaAdminProperties;

    @Autowired
    public KafkaAdminConfiguration(SchedulerKafkaAdminProperties kafkaAdminProperties) {
        this.kafkaAdminProperties = kafkaAdminProperties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdminProperties.getBrokerUrl());
        props.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaAdminProperties.getReconnectBackoffMs());
        props.put(AdminClientConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, kafkaAdminProperties.getReconnectBackoffMaxMs());
        props.put(AdminClientConfig.RETRIES_CONFIG, kafkaAdminProperties.getReconnectBackoffMaxMs());
        return new KafkaAdmin(props);
    }

    @Bean
    public KafkaAdmin.NewTopics jobsTopics() {
        NewTopic[] topics = WORKER_TYPES.stream()
                .map(type -> new NewTopic(
                        kafkaAdminProperties.getJobsTopicsPrefix() + type,
                        kafkaAdminProperties.getJobsTopicsPartitions(),
                        kafkaAdminProperties.getReplicationFactor()
                )).toArray(NewTopic[]::new);

        return new KafkaAdmin.NewTopics(topics);
    }

    @Bean
    public NewTopic runningTopic() {
        return new NewTopic(
                kafkaAdminProperties.getRunningTopicName(),
                kafkaAdminProperties.getRunningTopicPartitions(),
                kafkaAdminProperties.getReplicationFactor()
        );
    }

    @Bean
    public NewTopic finishedTopic() {
        return new NewTopic(
                kafkaAdminProperties.getFinishedTopicName(),
                kafkaAdminProperties.getFinishedTopicPartitions(),
                kafkaAdminProperties.getReplicationFactor()
        );
    }

    @Bean
    public WorkerTypesService workerTypesService() {
        return new WorkerTypesServiceImpl(new HashSet<>(WORKER_TYPES));
    }
}