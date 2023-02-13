package com.naumov.dotnetscriptsworker.config;

import com.naumov.dotnetscriptsworker.config.props.KafkaAdminProperties;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfiguration {
    private final KafkaAdminProperties kafkaAdminProperties;

    @Autowired
    public KafkaAdminConfiguration(KafkaAdminProperties kafkaAdminProperties) {
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
    public NewTopic jobsTopic() {
        return new NewTopic(
                kafkaAdminProperties.getJobsTopicName(),
                kafkaAdminProperties.getJobsTopicPartitions(),
                kafkaAdminProperties.getReplicationFactor()
        );
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
}
