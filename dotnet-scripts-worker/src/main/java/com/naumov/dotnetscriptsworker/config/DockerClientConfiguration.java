package com.naumov.dotnetscriptsworker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.naumov.dotnetscriptsworker.config.props.DockerClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerClientConfiguration {
    private final DockerClientProperties dockerClientProperties;

    @Autowired
    public DockerClientConfiguration(DockerClientProperties dockerClientProperties) {
        this.dockerClientProperties = dockerClientProperties;
    }

    @Bean
    public DockerClientConfig dockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerClientProperties.getDockerHost())
                .withDockerTlsVerify(dockerClientProperties.getDockerTlsVerify())
                .withDockerCertPath(dockerClientProperties.getDockerCertPath())
                .withRegistryUsername(dockerClientProperties.getRegistryUser())
                .withRegistryPassword(dockerClientProperties.getRegistryPassword())
                .withRegistryEmail(dockerClientProperties.getRegistryEmail())
                .withRegistryUrl(dockerClientProperties.getRegistryUrl())
                .build();
    }

    @Bean
    public DockerHttpClient dockerHttpClient() {
        DockerClientConfig config = dockerClientConfig();
        return new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(dockerClientProperties.getMaxConnections())
                .connectionTimeout(Duration.ofSeconds(dockerClientProperties.getConnectionTimeoutSec()))
                .responseTimeout(Duration.ofSeconds(dockerClientProperties.getResponseTimeoutSec()))
                .build();
    }

    @Bean
    public DockerClient dockerClient() {
        return DockerClientImpl.getInstance(dockerClientConfig(), dockerHttpClient());
    }
}
