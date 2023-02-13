package com.naumov.dotnetscriptsworker.config.props;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("worker.sandbox.container")
@Component
public class SandboxContainerProperties {
    @NotBlank
    private String imageName;

    @NotBlank
    private String imageTag;

    @NotBlank
    private String image;

    @NotBlank
    private String namePrefix;

    @NotNull
    private Boolean enableResourceLimits;

    /**
     * Docker "--memory" option
     */
    @NotNull
    @Min(128)
    private Long memoryMb;
    /**
     * Docker "--memory-reservation" option
     */
    @NotNull
    @Min(128)
    private Long memoryReservationMb;

    /**
     * Docker "--cpu-period" option
     */
    @NotNull
    @Min(1)
    private Long cpuPeriodMicros;

    /**
     * Docker "--cpu-quota" option
     */
    @NotNull
    @Min(1)
    private Long cpuQuotaMicros;

    /**
     * Docker "--cpu-shares" option
     */
    @NotNull
    @Min(1)
    @Max(1024)
    private Integer cpuShares;

    /**
     * Docker "--pids-limit" option
     */
    @NotNull
    @Min(1)
    @Max(100)
    private Long pidsLimit;

    /**
     * Docker "--blkio-weight" option
     */
    @NotNull
    @Min(10)
    @Max(1000)
    private Integer blkioWeight;

    /**
     * Value for docker "--storage-opt size" option
     */
    @NotBlank
    private String storageSize;
}
