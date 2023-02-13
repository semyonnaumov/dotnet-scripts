package com.naumov.dotnetscriptsworker;

import com.naumov.dotnetscriptsworker.service.ContainerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:8097", "port=8097"})
class DotnetScriptsWorkerApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Configuration
    public static class TestConfig {

        @Primary
        @Bean
        ContainerService containerService() {
            ContainerService mock = mock(ContainerService.class);
            when(mock.getAllContainersIdsWithNamePrefix(anyString())).thenReturn(Collections.emptyList());
            when(mock.getAllContainersIds()).thenReturn(Collections.emptyList());
            return mock;
        }
    }
}
