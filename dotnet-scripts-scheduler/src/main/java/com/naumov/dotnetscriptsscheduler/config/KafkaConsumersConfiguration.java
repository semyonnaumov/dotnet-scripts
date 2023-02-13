package com.naumov.dotnetscriptsscheduler.config;

import com.naumov.dotnetscriptsscheduler.config.props.SchedulerKafkaProperties;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobFinishedMessage;
import com.naumov.dotnetscriptsscheduler.dto.kafka.cons.JobStartedMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumersConfiguration implements KafkaListenerConfigurer {
    private static final int CONSUMER_CONCURRENCY = 1;
    private static final ContainerProperties.AckMode CONSUMER_ACK_MODE = ContainerProperties.AckMode.MANUAL_IMMEDIATE;
    private final SchedulerKafkaProperties kafkaProperties;
    private final LocalValidatorFactoryBean validator;

    @Autowired
    public KafkaConsumersConfiguration(SchedulerKafkaProperties kafkaProperties, LocalValidatorFactoryBean validator) {
        this.kafkaProperties = kafkaProperties;
        this.validator = validator;
    }

    @Bean
    public Map<String, Object> commonConsumerProperties() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBrokerUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumerGroup());
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaProperties.getReconnectBackoffMs());
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, kafkaProperties.getReconnectBackoffMaxMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.naumov.*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, JobStartedMessage> jobStartedMessagesConsumerFactory() {
        Map<String, Object> props = new HashMap<>(commonConsumerProperties());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JobStartedMessage.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, JobFinishedMessage> jobFinishedMessagesConsumerFactory() {
        Map<String, Object> props = new HashMap<>(commonConsumerProperties());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JobFinishedMessage.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobStartedMessage> jobStartedMessagesKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, JobStartedMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(jobStartedMessagesConsumerFactory());
        factory.setConcurrency(CONSUMER_CONCURRENCY);
        factory.getContainerProperties().setAckMode(CONSUMER_ACK_MODE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobFinishedMessage> jobFinishedMessagesKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, JobFinishedMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(jobFinishedMessagesConsumerFactory());
        factory.setConcurrency(CONSUMER_CONCURRENCY);
        factory.getContainerProperties().setAckMode(CONSUMER_ACK_MODE);

        return factory;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setValidator(this.validator);
    }
}
