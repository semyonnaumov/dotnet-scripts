package com.naumov.dotnetscriptsworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DotnetScriptsWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DotnetScriptsWorkerApplication.class, args);
    }

}
