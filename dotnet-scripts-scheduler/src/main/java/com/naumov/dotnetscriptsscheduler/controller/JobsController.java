package com.naumov.dotnetscriptsscheduler.controller;

import com.naumov.dotnetscriptsscheduler.dto.rest.RestDtoMapper;
import com.naumov.dotnetscriptsscheduler.dto.rest.rq.JobCreateRequest;
import com.naumov.dotnetscriptsscheduler.dto.rest.rs.*;
import com.naumov.dotnetscriptsscheduler.exception.BadInputException;
import com.naumov.dotnetscriptsscheduler.model.Job;
import com.naumov.dotnetscriptsscheduler.model.JobCreationResult;
import com.naumov.dotnetscriptsscheduler.model.JobRequest;
import com.naumov.dotnetscriptsscheduler.model.JobResult;
import com.naumov.dotnetscriptsscheduler.model.JobStatus;
import com.naumov.dotnetscriptsscheduler.service.JobService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/jobs", produces = APPLICATION_JSON_VALUE)
@Validated
public class JobsController {
    private static final Logger LOGGER = LogManager.getLogger(JobsController.class);
    private final JobService jobService;
    private final RestDtoMapper dtoMapper;

    @Autowired
    public JobsController(JobService jobService, RestDtoMapper restDtoMapper) {
        this.jobService = jobService;
        this.dtoMapper = restDtoMapper;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<JobCreateResponse> createJob(@Valid @RequestBody JobCreateRequest jobCreateRequest) {
        LOGGER.info("Received job creation request {}", jobCreateRequest.getRequestId());
        JobCreationResult jobCreationResult = jobService.createOrGetJob(dtoMapper.fromJobCreateRequest(jobCreateRequest));
        HttpStatus responseStatus = jobCreationResult.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(responseStatus).body(dtoMapper.toJobCreateResponse(jobCreationResult.getJob()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobGetResponse> getJob(@NotNull @PathVariable("id") UUID jobId) {
        LOGGER.info("Received full job info request for job {}", jobId);
        Optional<Job> jobOptional = jobService.findJob(jobId);

        return jobOptional.map(job -> ResponseEntity.status(HttpStatus.OK).body(dtoMapper.toJobGetResponse(job)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/request")
    public ResponseEntity<JobGetRequestResponse> getJobRequest(@NotNull @PathVariable("id") UUID jobId) {
        LOGGER.info("Received job request request for job {}", jobId);
        Optional<JobRequest> jobRequestOptional = jobService.findJobRequestByJobId(jobId);

        return jobRequestOptional.map(jr -> ResponseEntity.status(HttpStatus.OK)
                        .body(dtoMapper.toJobGetRequestResponse(jobId, jr)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<JobGetStatusResponse> getJobStatus(@NotNull @PathVariable("id") UUID jobId) {
        LOGGER.info("Received job status request for job {}", jobId);
        Optional<JobStatus> jobStatusOptional = jobService.findJobStatusByJobId(jobId);

        return jobStatusOptional.map(js -> ResponseEntity.status(HttpStatus.OK)
                        .body(dtoMapper.toJobGetStatusResponse(jobId, js)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<JobGetResultResponse> getJobResult(@NotNull @PathVariable("id") UUID jobId) {
        LOGGER.info("Received job result request for job {}", jobId);
        Optional<JobResult> jobResultOptional = jobService.findJobResultByJobId(jobId);

        return jobResultOptional.map(jr -> ResponseEntity.status(HttpStatus.OK)
                        .body(dtoMapper.toJobGetResultResponse(jobId, jr)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@NotNull @PathVariable("id") UUID jobId) {
        LOGGER.info("Received job deletion request for job {}", jobId);
        if (jobService.deleteJob(jobId)) return ResponseEntity.status(HttpStatus.OK).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            BadInputException.class
    })
    public ResponseEntity<DefaultErrorResponse> handleBadRequestExceptions(Exception e) {
        LOGGER.info("Received bad request", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultErrorResponse> handleAllOtherExceptions(Exception e) {
        LOGGER.error("Failed to process request", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }
}
