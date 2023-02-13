package com.naumov.dotnetscriptsscheduler;

import com.naumov.dotnetscriptsscheduler.repository.JobsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DotnetScriptsSchedulerApplicationTest extends AbstractIntegrationTest {
    @Autowired
    private JobsRepository jobsRepository;

    @Transactional
    @Test
    public void contextLoads() {
        assertTrue(jobsRepository.findJobRequestByJobId(UUID.randomUUID()).isEmpty());
    }
}